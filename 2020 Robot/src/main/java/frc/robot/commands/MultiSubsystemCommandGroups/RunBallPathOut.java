/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.MultiSubsystemCommandGroups;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import frc.robot.commands.FloorIntake.RunFloorIntakeOut;
import frc.robot.commands.Sorter.RunSorterBack;
import frc.robot.commands.Tower.RunTowerOut;
import frc.robot.subsystems.FloorIntake;
import frc.robot.subsystems.Sorter;
import frc.robot.subsystems.Tower;

// NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// information, see:
// https://docs.wpilib.org/en/latest/docs/software/commandbased/convenience-features.html
public class RunBallPathOut extends ParallelCommandGroup {
  /**
   * Creates a new RunBallPathOut.
   */
  private FloorIntake mFloorIntake;
  private Sorter mSorter;
  private Tower mTower;

  public RunBallPathOut(FloorIntake floorIntake, Sorter sorter, Tower tower) {
    // Add your commands in the super() call, e.g.
    // super(new FooCommand(), new BarCommand());super();
    mFloorIntake = floorIntake;
    mSorter = sorter;
    mTower = tower;

    addCommands(
      // new RunFloorIntakeOut(mFloorIntake),

      new RunSorterBack(mSorter),

      new RunTowerOut(mTower)
    );
  }
}
