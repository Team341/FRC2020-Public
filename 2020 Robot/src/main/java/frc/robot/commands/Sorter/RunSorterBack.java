/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.Sorter;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Sorter;

public class RunSorterBack extends CommandBase {
  /**
   * Creates a new RunSorter.
   */

  private Sorter mSorter;

  public RunSorterBack(Sorter sorter) {
    // Use addRequirements() here to declare subsystem dependencies.

    mSorter = sorter;
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    mSorter.runBackwards();
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    mSorter.runBackwards();
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    mSorter.setSpeed(0.0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}