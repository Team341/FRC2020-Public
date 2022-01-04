/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.MultiSubsystemCommandGroups;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import frc.robot.subsystems.Tower;
import frc.robot.subsystems.Sorter;
import frc.robot.commands.FloorIntake.RunFloorIntake;
import frc.robot.commands.Sorter.RunSorter;
import frc.robot.commands.Tower.RunTower;
import frc.robot.subsystems.FloorIntake;

// NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// information, see:
// https://docs.wpilib.org/en/latest/docs/software/commandbased/convenience-features.html
public class RunBallPath extends ParallelCommandGroup {
  /**
   * Creates a new RunBallPath.
   */

  private FloorIntake mFloorIntake;
  private Sorter mSorter;
  private Tower mTower;
  
  public RunBallPath(FloorIntake floorIntake, Sorter sorter, Tower tower) {
    // Add your commands in the super() call, e.g.
    // super(new FooCommand(), new BarCommand());super();

    mFloorIntake = floorIntake;
    mSorter = sorter;
    mTower = tower;

    addCommands(
      new RunFloorIntake(mFloorIntake),

      new RunSorter(mSorter, mTower),

      new RunTower(mTower)
    );
  }
}
