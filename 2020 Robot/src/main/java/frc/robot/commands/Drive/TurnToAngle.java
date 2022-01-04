/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.Drive;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj2.command.PIDCommand;
import frc.robot.Constants;
import frc.robot.subsystems.Drive;
import frc.robot.utilities.DaisyMath;

// NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// information, see:
// https://docs.wpilib.org/en/latest/docs/software/commandbased/convenience-features.html

public class TurnToAngle extends PIDCommand {
  /**
   * Creates a new turnToAngle.
   */
  
  private Drive mDrive;

  private double mGoalHeadingInDegrees;
  
  public TurnToAngle(double goalHeadingInDegrees, Drive drive, DoubleSupplier mRightStickX) {
    super(
        // The controller that the command will use
        new PIDController(Constants.Drive.DEGREE_P, Constants.Drive.DEGREE_I, Constants.Drive.DEGREE_D),
        // This should return the measurement
        () -> drive.getGyroAngle(),
        // This should return the setpoint (can also be a constant)
        () -> goalHeadingInDegrees,
        // This uses the output
        output -> {
          // Use the output here
          output = Math.signum(output) * DaisyMath.minmax(Math.abs(output), 0.0, 0.5);
          drive.getDifferentialDrive().arcadeDrive(mRightStickX.getAsDouble(), -output);
          
        });
    // Use addRequirements() here to declare subsystem dependencies.
    // Configure additional PID options by calling `getController` here.
    mDrive = drive;
    addRequirements(mDrive);
    mGoalHeadingInDegrees = goalHeadingInDegrees;
  }

  @Override
  public void initialize() {
    System.out.println("Starting turn to angle " + mGoalHeadingInDegrees + " Current Angle " + mDrive.getGyroAngle());
    super.initialize();
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    double error = Math.abs( DaisyMath.boundAngleNeg180to180Degrees(mGoalHeadingInDegrees - mDrive.getGyroAngle()));
    if (error <= Constants.Drive.ANGLE_TOLERANCE) {
      System.out.println("Done turning to angle " + mGoalHeadingInDegrees + " With Error " + error);
      return true;
    }
    return false;
  }
  
}
