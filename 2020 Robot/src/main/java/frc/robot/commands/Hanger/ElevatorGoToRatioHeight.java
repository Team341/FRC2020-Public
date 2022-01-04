/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.Hanger;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Hanger;

public class ElevatorGoToRatioHeight extends CommandBase {
  /**
   * Creates a new ElevatorGoToRatioHeight.
   */
  private Hanger mHanger;
  private double mStartingHeight;
  public ElevatorGoToRatioHeight(Hanger hanger) {
    // Use addRequirements() here to declare subsystem dependencies.
    mHanger = hanger;
    mStartingHeight = mHanger.getHangerHeightInInches();
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    mHanger.goToHeight(mStartingHeight / 4.0);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    if (Math.abs(mHanger.getHangerHeightInInches() - (mStartingHeight / 4.0)) < 2.0) {
      return true;
    }
    return false;
  }
}
