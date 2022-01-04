package frc.robot.commands.Autonomous;

import java.io.IOException;
import java.nio.file.Path;

import org.apache.commons.lang3.ObjectUtils.Null;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.controller.RamseteController;
import edu.wpi.first.wpilibj.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Transform2d;
import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj.trajectory.TrajectoryUtil;
import edu.wpi.first.wpilibj2.command.RamseteCommand;
import edu.wpi.first.wpilibj2.command.Subsystem;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.subsystems.Drive;

public class FollowPath {

    private static Drive mDrive;
    private Path mPath;
    
    private Boolean mReversed;
    
    private Trajectory testTrajectory;
    public Transform2d transform;

    private RamseteCommand DriveForwardAutonomousCommand;

    private RamseteController disabledRamsete;

    private PIDController leftPIDController;
    private PIDController rightPIDController;

    public FollowPath(Path path, Drive drive, boolean reversed) {
        mDrive = drive;
        mPath = path;

        mReversed = reversed;
        getTrajectory();

        leftPIDController = new PIDController(Constants.Drive.kPDriveVel, 0, 0);
        rightPIDController = new PIDController(Constants.Drive.kPDriveVel, 0, 0);

        disabledRamsete = new RamseteController() {
            @Override
            public ChassisSpeeds calculate(Pose2d currentPose, Pose2d poseRef, double linearVelocityRefMeters, double angleVelocityRefRadiansPerSecond) {
                return new ChassisSpeeds(linearVelocityRefMeters, 0.0, angleVelocityRefRadiansPerSecond);
            }
        };
    }

    // Create a voltage constraint to ensure we don't accelerat too fast
    // var autoVoltageConstraint = 
    //     new DifferentialDriveVoltageConstraint(
    //         new SimpleMotorFeedforward(Constants.Drive.ksVolts,
    //                                    Constants.Drive.kvVoltSecondsPerMeter,
    //                                    Constants.Drive.kaVoltSecondsSquaredPerMeter),
    //         Constants.Drive.kDriveKinematics,
    //         10);

    // // Create config for trajectory
    // TrajectoryConfig config =
    //     new TrajectoryConfig(Constants.Drive.kMaxSpeedMetersPerSecond,
    //                          Constants.Drive.kMaxAccelerationMetersPerSecondSquared)
    //         // Add kinematics to ensure max speed is actually obeyed
    //         .setKinematics(Constants.Drive.kDriveKinematics)
    //         // Apply the voltage constraint
    //         .addConstraint(autoVoltageConstraint);

    /**
     * @return gets the trajectory from the defined path
     */
    public Trajectory getTrajectory() {
        try {
            testTrajectory = TrajectoryUtil.fromPathweaverJson(mPath);
            return testTrajectory;
        } catch (IOException ex) {
            DriverStation.reportError("Unable to open trajectory: " + mPath, ex.getStackTrace());
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * get the trajectory modified so that the robot is at the starting point
     * @return transformed trajectory with the robot at the starting point. 
     */
    public Trajectory getTrajectoryFromStartingPoint() {
        transform = mDrive.getPose().minus(getTrajectory().getInitialPose());
        return testTrajectory.transformBy(transform);
    }

    public RamseteCommand getDriveForwardRamseteCommand() {
        // Reset the encoders & odometry to ensure we start at the begining of the path
        // mDrive.resetOdometry();

        if (mReversed) {
            // Sort of hack-y, but invert the commands sent to the drive motors and 
            // invert the encoders and multiply by -1

            DriveForwardAutonomousCommand = new RamseteCommand(
                getTrajectoryFromStartingPoint(),
                mDrive::getInvertedPose,
                // disabledRamsete,
                // Ramsete controllers are used to follow paths smoothly, with both turning and ramping up/down
                // This uses the ramsete b and zeta, these fill similar roles as P and D in PIDs
                // Should be about 2.0 b and 0.7 zeta for every robot

                new RamseteController(Constants.Auto.kRamseteB, 
                                      Constants.Auto.kRamseteZeta),

                new SimpleMotorFeedforward(Constants.Drive.ksVolts,
                                        Constants.Drive.kvVoltSecondsPerMeter,
                                        Constants.Drive.kaVoltSecondsSquaredPerMeter),

                Constants.Drive.kDriveKinematics,
                mDrive::getInvertedDifferentialDriveSpeed,

                leftPIDController,
                rightPIDController,

                // RamseteCommand passes volts to the callback
                mDrive::setInvertedTankDriveVolts,
                mDrive
            );
        } else {
            DriveForwardAutonomousCommand = new RamseteCommand(
                getTrajectoryFromStartingPoint(),
                mDrive::getPose,

                // Ramsete controllers are used to follow paths smoothly, with both turning and ramping up/down
                // This uses the ramsete b and zeta, these fill similar roles as P and D in PIDs
                // Should be about 2.0 b and 0.7 zeta for every robot
                // disabledRamsete,
                new RamseteController(Constants.Auto.kRamseteB, 
                                    Constants.Auto.kRamseteZeta),

                new SimpleMotorFeedforward(Constants.Drive.ksVolts,
                                        Constants.Drive.kvVoltSecondsPerMeter,
                                        Constants.Drive.kaVoltSecondsSquaredPerMeter),

                Constants.Drive.kDriveKinematics,
                mDrive::getDifferentialDriveSpeed,

                leftPIDController,
                rightPIDController,

                // RamseteCommand passes volts to the callback
                mDrive::setTankDriveVolts,
                mDrive
            );
        }

        // DriveForwardAutonomousCommand.beforeStarting(() -> mDrive.resetOdometry(), new NullSubsystem());
        return DriveForwardAutonomousCommand;
    }
    

    public void logToDashboard() {
        SmartDashboard.putNumber("Autonomous/Drive Left Error", leftPIDController.getVelocityError());
        SmartDashboard.putNumber("Autonomous/Drive Right Error", rightPIDController.getVelocityError());

        if (!mReversed) {
            SmartDashboard.putNumber("Autonomous/Left Velocity", mDrive.getDifferentialDriveSpeed().leftMetersPerSecond);

            SmartDashboard.putNumber("Autonomous/Right Velocity", mDrive.getDifferentialDriveSpeed().rightMetersPerSecond);
            
        } else {
            SmartDashboard.putNumber("Autonomous/Left Velocity", mDrive.getInvertedDifferentialDriveSpeed().leftMetersPerSecond);

            SmartDashboard.putNumber("Autonomous/Right Velocity", mDrive.getInvertedDifferentialDriveSpeed().rightMetersPerSecond);
        }
        
        SmartDashboard.putNumber("Autonomous/Left Controller Set Point", leftPIDController.getSetpoint());
        SmartDashboard.putNumber("Autonomous/Right Controller Set Point", rightPIDController.getSetpoint());

    }
}