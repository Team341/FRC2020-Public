/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.FloorIntake;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.FloorIntake;

public class RunFloorIntake extends CommandBase {
  /**
   * Creates a new runFloorIntake.
   */
  private FloorIntake mFloorIntake;
  private boolean mIntakeState;
  private boolean mLoweredIntake;
  public RunFloorIntake(FloorIntake floor) {
    // Use addRequirements() here to declare subsystem dependencies.
    mFloorIntake = floor;
    addRequirements(mFloorIntake);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    mIntakeState = mFloorIntake.getFloorIntakeState();
    if (!mIntakeState) {
      mLoweredIntake = true;
      mFloorIntake.setFloorIntakeState(true);
    } else {
      mLoweredIntake = false;
    }
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    mFloorIntake.runIntake();
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    mFloorIntake.setIntakeSpeed(0.0);
    if (mLoweredIntake) {
      mFloorIntake.setFloorIntakeState(false);
    }
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
