/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.Drive;

import edu.wpi.first.wpilibj.controller.ProfiledPIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj2.command.ProfiledPIDCommand;

import frc.robot.Constants;
import frc.robot.subsystems.Drive;
import frc.robot.utilities.DaisyMath;

/**
 * A command that will turn the robot to the specified angle using a motion profile.
 */
public class TurnToAngleProfiled extends ProfiledPIDCommand {
  /**
   * Creates a new TurnToAngleProfiled.
   */
  
  /**
   * Turns to robot to the specified angle using a motion profile.
   *
   * @param targetAngleDegrees The angle to turn to
   * @param drive              The drive subsystem to use
   */

   
  private int count = 0;
  public TurnToAngleProfiled(double targetAngleDegrees, Drive drive) {

    super(
        new ProfiledPIDController(
            Constants.Drive.PROFILED_TURN_P, 
            Constants.Drive.PROFILED_TURN_I,
            Constants.Drive.PROFILED_TURN_D, 
            new TrapezoidProfile.Constraints(
                Constants.Drive.MAX_TURN_RATE_DEG_PER_S,
                Constants.Drive.MAX_TURN_ACCELERATION_DEG_PER_S_SQUARED
            )
        ),
        // Close loop on heading
        () -> drive.getHeading().getDegrees(),
        // Set reference to target
        targetAngleDegrees,
        // Pipe output to turn robot
        (output, setpoint) -> drive.getDifferentialDrive().arcadeDrive(0, DaisyMath.minmax(-Math.signum(output)*Math.max(Math.abs(output), 0.154), -1.0, 1.0)),
        
        // Require the drive
        drive
    );
    // Set the controller to be continuous (because it is an angle controller)
    getController().enableContinuousInput(-180, 180);
    // Set the controller tolerance - the delta tolerance ensures the robot is stationary at the
    // setpoint before it is considered as having reached the reference
    getController().setTolerance(Constants.Drive.TURN_TOLERANCE_DEG, Constants.Drive.TURN_RATE_TOLERANCE_DEG_PER_S);

    SmartDashboard.putNumber("TurnPID/P", Constants.Drive.PROFILED_TURN_P);
    SmartDashboard.putNumber("TurnPID/I", Constants.Drive.PROFILED_TURN_I);
    SmartDashboard.putNumber("TurnPID/D", Constants.Drive.PROFILED_TURN_D);
  }

 @Override
 public void initialize() {
   // TODO Auto-generated method stub
   getController().reset(Drive.getInstance().getHeading().getDegrees());
   double p = SmartDashboard.getNumber("TurnPID/P", Constants.Drive.PROFILED_TURN_P);
   double i = SmartDashboard.getNumber("TurnPID/I", Constants.Drive.PROFILED_TURN_I);
   double d = SmartDashboard.getNumber("TurnPID/D", Constants.Drive.PROFILED_TURN_D);
   getController().setPID(p, i, d);
   
  count = 0;
  //  Drive.getInstance().resetGyro();
   super.initialize();
 }

  @Override
  public void end(boolean interrupted) {
    // TODO Auto-generated method stub
    super.end(interrupted);
    Drive.getInstance().setSpeed(0.0, 0.0);
  }

  @Override
  public boolean isFinished() {
    // End when the controller is at the reference.
    SmartDashboard.putNumber("TurnPID/PosError", getController().getPositionError());
    SmartDashboard.putNumber("TurnPID/VelError", getController().getVelocityError());
    SmartDashboard.putNumber("TurnPID/Goal", getController().getGoal().position);
    SmartDashboard.putNumber("TurnPID/Setpoint", getController().getSetpoint().position);
    SmartDashboard.putNumber("TurnPID/Supplied Angle", Drive.getInstance().getHeading().getDegrees());
    SmartDashboard.putNumber("TurnPID/Output", getController().calculate(Drive.getInstance().getHeading().getDegrees()));
    SmartDashboard.putNumber("TurnPID/Count", count);
    
    if (getController().atGoal()) {
      count++;
    } else {
      count = 0;
    }

    return count > 10;
  }
}
