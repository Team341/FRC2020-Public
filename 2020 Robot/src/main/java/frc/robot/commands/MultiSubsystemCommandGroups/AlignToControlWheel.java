/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.MultiSubsystemCommandGroups;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.subsystems.ControlWheel;
import frc.robot.subsystems.Drive;

public class AlignToControlWheel extends CommandBase {
  /**
   * Creates a new AlignToControlWheel.
   */

  private Drive mDrive;
  private ControlWheel mControlWheel;
  private DoubleSupplier mDriverLeftY;

  public AlignToControlWheel(Drive drive, ControlWheel controlWheel, DoubleSupplier driverLeftY) {
    // Use addRequirements() here to declare subsystem dependencies.
    mDrive = drive;
    mControlWheel = controlWheel;

    addRequirements(mDrive, mControlWheel);

    mDriverLeftY = driverLeftY;
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    mControlWheel.setControlWheelSolenoidState(true);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    double leftSpeed = Constants.ControlWheel.ALIGN_TO_CONTROL_WHEEL_SPEED; 
    double rightSpeed = Constants.ControlWheel.ALIGN_TO_CONTROL_WHEEL_SPEED;
    
    if (mControlWheel.getLimitSwitchLeft()) {
      leftSpeed = 0.0;
    }
    if (mControlWheel.getLimitSwitchRight()) {
      rightSpeed = 0.0;
    }
    mDrive.getDifferentialDrive().tankDrive(leftSpeed, rightSpeed);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    mDrive.getDifferentialDrive().arcadeDrive(0.0, 0.0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    if (mControlWheel.getLimitSwitchLeft() && mControlWheel.getLimitSwitchRight()) {
      return true;
    }
    return false;
  }
}
