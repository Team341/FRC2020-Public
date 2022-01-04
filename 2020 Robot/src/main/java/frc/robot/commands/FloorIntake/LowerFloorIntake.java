/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.FloorIntake;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.FloorIntake;;

public class LowerFloorIntake extends CommandBase {
  /**
   * Creates a new lowerFloorIntake.
   */

  private FloorIntake mFloorIntake;
  private boolean isFloorIntakeLowered;

  public LowerFloorIntake(FloorIntake floorIntake) {
    // Use addRequirements() here to declare subsystem dependencies.
    mFloorIntake = floorIntake;
    
    addRequirements(mFloorIntake);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    isFloorIntakeLowered = mFloorIntake.getFloorIntakeState();
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    if (!isFloorIntakeLowered) {
      mFloorIntake.setFloorIntakeState(true);
    }
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    if (mFloorIntake.getFloorIntakeState()) {
      return true;
    }
    return false;
  }
}
