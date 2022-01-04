/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.ColorWheel;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.subsystems.ControlWheel;

public class PositionControl extends CommandBase {
  /**
   * Creates a new PositionControl.
   */
  private ControlWheel mControlWheel;
  private boolean mFinished;
  private int mColorSetPoint;
  private int onTargetCount;

  public PositionControl(ControlWheel ControlWheel) {
    // Use addRequirements() here to declare subsystem dependencies.
    mControlWheel = ControlWheel;

    onTargetCount = 0;
    addRequirements(mControlWheel);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    mColorSetPoint = mControlWheel.getSmartDashboardColor();
    if (mColorSetPoint == 99) {
      mFinished = true;
    } else {
      mFinished = false;
    }
    SmartDashboard.putNumber("ControlWheel/Color Set Point", mColorSetPoint);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    // TODO this needs to get it from the field management thing or whatever. 
    if (mControlWheel.getColor() != mControlWheel.getColorFromInt(mColorSetPoint)){
      mControlWheel.setSpeed(Constants.ControlWheel.DEFAULT_CONTROL_WHEEL_SPEED/2);
      onTargetCount = 0;
    } else {
      mControlWheel.setSpeed(0.0);
      onTargetCount++;
    }
    if (onTargetCount > 15) {
      mFinished = true;
    } else {
      mFinished = false;
    }
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    mControlWheel.setSpeed(0.0);
    mControlWheel.setControlWheelSolenoidState(false);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return mFinished;
  }
}