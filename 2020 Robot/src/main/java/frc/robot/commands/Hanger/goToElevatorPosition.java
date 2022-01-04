/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.Hanger;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.subsystems.Hanger;

public class goToElevatorPosition extends CommandBase {
  /**
   * Creates a new goToElevatorPosition.
   */
  private Hanger mHanger;
  public goToElevatorPosition(Hanger hanger) {
    // Use addRequirements() here to declare subsystem dependencies.
    mHanger = hanger;
    addRequirements(mHanger);
  }

  // Called when the command is initially scheduled.
  private double goalHeight;
  @Override
  public void initialize() {
    switch (Hanger.elevatorPosition) {
      case 1: goalHeight = Constants.HangerHeights.LOW_BAR_HEIGHT; 
        break;
      case 2: goalHeight = Constants.HangerHeights.MID_BAR_HEIGHT; 
        break;
      case 3: goalHeight = Constants.HangerHeights.HIGH_BAR_HEIGHT; 
        break;
      default: goalHeight = 0.0;
        break;
    }
    mHanger.goToHeight(goalHeight);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    mHanger.setElevatorPosition(mHanger.getElevatorPositionNumber() + 1);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    if (Math.abs(goalHeight - mHanger.getHangerHeightInInches()) < Constants.Hanger.HANGER_DISTANCE_TOLERANCE_INCHES) {
      return true;
    }
    return false;
  }
}
