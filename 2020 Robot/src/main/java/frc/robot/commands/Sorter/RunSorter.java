/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.Sorter;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Sorter;
import frc.robot.subsystems.Tower;

public class RunSorter extends CommandBase {
  /**
   * Creates a new RunSorter.
   */

  private Sorter mSorter;
  private Tower mTower;

  public RunSorter(Sorter sorter, Tower tower) {
    // Use addRequirements() here to declare subsystem dependencies.

    mSorter = sorter;
    mTower = tower;

    addRequirements(mSorter);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    mSorter.setSpeed(1.0);
    logToDashboard(true);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    if (mTower.getBeamBreakOneState()) {
      mSorter.setSpeed(0.0);
    } else {
      mSorter.setSpeed(1.0);
    }
    logToDashboard(true);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    mSorter.setSpeed(0.0);
    logToDashboard(false);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
  
  public void logToDashboard(boolean isSorterBeingCalled) {
    SmartDashboard.putBoolean("Is run sorter being called", isSorterBeingCalled);
  }
}
