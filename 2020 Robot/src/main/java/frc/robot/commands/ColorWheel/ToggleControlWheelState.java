/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.ColorWheel;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.ControlWheel;
import frc.robot.subsystems.Hanger;;

public class ToggleControlWheelState extends CommandBase {
  /**
   * Creates a new toggleControlWheelState.
   */
  private ControlWheel mControlWheel;
  private Hanger mHanger;

  private boolean mControlWheelState;
  
  public ToggleControlWheelState(ControlWheel controlWheel, Hanger hanger) {
    // Use addRequirements() here to declare subsystem dependencies.
    mControlWheel = controlWheel;
    mHanger = hanger;

    addRequirements(mControlWheel);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    if (mHanger.isElevatorStowed() && !mControlWheel.getControlWheelSolenoidState()) {
      mControlWheelState = true;
    } else {
      mControlWheelState = false;
    }
    mControlWheel.setControlWheelSolenoidState(mControlWheelState);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return true;
  }
}
