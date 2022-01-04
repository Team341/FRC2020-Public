/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.Hanger;

import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj2.command.PIDCommand;
import frc.robot.Constants;
import frc.robot.subsystems.Hanger;

// NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// information, see:
// https://docs.wpilib.org/en/latest/docs/software/commandbased/convenience-features.html
public class ExtendHangerToHeight extends PIDCommand {
  /**
   * Creates a new extendHangerToHeight.
   */

  private double goalHeightInInches;
  private Hanger mHanger;
  private double mTolerance;
  public ExtendHangerToHeight(double goalHeightInInches, Hanger hanger, double tolerance) {
    super(
        // The controller that the command will use
        new PIDController(Hanger.kP, Hanger.kI, Hanger.kD),
        // This should return the measurement
        () -> hanger.getHangerHeightInInches(),
        // This should return the setpoint (can also be a constant)
        () -> goalHeightInInches,
        // This uses the output
        output -> {
          hanger.setElevatorSpeed(output);
        });
    // Use addRequirements() here to declare subsystem dependencies.
    // Configure additional PID options by calling `getController` here.
    mHanger = hanger;
    mTolerance = tolerance;
    addRequirements(mHanger);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    if (Math.abs(
        goalHeightInInches - mHanger.getHangerHeightInInches()) > mTolerance) {
      return true;
    }
    return false;
  }
}