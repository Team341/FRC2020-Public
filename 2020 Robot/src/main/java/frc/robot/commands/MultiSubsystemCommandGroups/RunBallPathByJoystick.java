/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.MultiSubsystemCommandGroups;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import frc.robot.commands.FloorIntake.RunFloorIntakeByJoystick;
import frc.robot.commands.Sorter.RunSorterByJoystick;
import frc.robot.commands.Tower.RunTowerByJoystick;
import frc.robot.subsystems.FloorIntake;
import frc.robot.subsystems.Sorter;
import frc.robot.subsystems.Tower;

// NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// information, see:
// https://docs.wpilib.org/en/latest/docs/software/commandbased/convenience-features.html
public class RunBallPathByJoystick extends ParallelCommandGroup {
  /**
   * Creates a new RunBallPathByJoystick.
   */
  private FloorIntake mFloorIntake;
  private Sorter mSorter;
  private Tower mTower;
  
  private DoubleSupplier mRightY;
  public RunBallPathByJoystick(FloorIntake floorIntake, Sorter sorter, Tower tower, DoubleSupplier rightY) {
    // Use addRequirements() here to declare subsystem dependencies.
    mFloorIntake = floorIntake;
    mSorter = sorter;
    mTower = tower;
    addRequirements(mFloorIntake, mSorter, mTower);

    mRightY = rightY;
    addCommands(
      new RunFloorIntakeByJoystick(mFloorIntake, mRightY),

      new RunSorterByJoystick(mSorter, mRightY),

      new RunTowerByJoystick(mTower, mRightY)
    );
  }
}
