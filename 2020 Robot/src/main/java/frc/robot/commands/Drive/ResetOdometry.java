/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.Drive;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Drive;

public class ResetOdometry extends CommandBase {
  /**
   * Creates a new ResetOdometry.
   */
  private Drive mDrive;
  private int count;
  public ResetOdometry(Drive drive) {
    // Use addRequirements() here to declare subsystem dependencies.
    mDrive = drive;
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    count = 0;
    mDrive.resetOdometry();
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    count++;
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return count >= 5;
  }
}
