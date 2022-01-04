/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.ColorWheel;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.subsystems.ControlWheel;

public class RotationControl extends CommandBase {
  /**
   * Creates a new RptationControl.
   */
  private ControlWheel mControlWheel;
  private int mIterate;
  private boolean mFinished;

  public RotationControl(ControlWheel ControlWheel) {
    // Use addRequirements() here to declare subsystem dependencies.
    mControlWheel = ControlWheel;
    addRequirements(mControlWheel);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    mIterate = 0;
    mControlWheel.setLastColor(mControlWheel.getColor());
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    if (mControlWheel.getColor() != mControlWheel.getLastColor()){
      mControlWheel.setLastColor(mControlWheel.getColor());
      mIterate++;
    }

    if (mIterate < 35){
      mControlWheel.setSpeed(Constants.ControlWheel.DEFAULT_CONTROL_WHEEL_SPEED);
      mFinished = false;
    } else {
      mControlWheel.setSpeed(0.0);
      mFinished = true;
    }
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    mControlWheel.setSpeed(0.0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return mFinished;
  }
}