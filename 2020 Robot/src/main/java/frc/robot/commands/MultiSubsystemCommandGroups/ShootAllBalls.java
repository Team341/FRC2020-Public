/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.MultiSubsystemCommandGroups;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import frc.robot.commands.Shooter.ShooterRPM;
import frc.robot.commands.Tower.EmptyTower;
import frc.robot.subsystems.Shooter;
import frc.robot.subsystems.Sorter;
import frc.robot.subsystems.Tower;

// NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// information, see:
// https://docs.wpilib.org/en/latest/docs/software/commandbased/convenience-features.html
public class ShootAllBalls extends ParallelCommandGroup {
  /**
   * Creates a new Shoot.
   */
  private Shooter mShooter;
  private Tower mTower;
  private Sorter mSorter;
  public ShootAllBalls(Shooter shooter,Tower tower, Sorter sorter) {
    // Add your commands in the super() call, e.g.
    // super(new FooCommand(), new BarCommand());super();

    mShooter = shooter;
    mTower = tower;
    mSorter = sorter;

    addCommands(
      new ShooterRPM(mShooter, mTower,  ()->(tower.getBeamBreakFourState() || tower.getBeamBreakThreeState() || tower.getBeamBreakTwoState() || tower.getBeamBreakOneState()), false),

      new EmptyTower(mTower, mShooter, mSorter)
    );
  }
}
