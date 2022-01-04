/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.Autonomous;

import java.nio.file.Paths;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.commands.Drive.TurnToAngle;
import frc.robot.commands.MultiSubsystemCommandGroups.Shoot;
import frc.robot.commands.MultiSubsystemCommandGroups.ShootAllBalls;
import frc.robot.commands.Shooter.ShooterRPM;
import frc.robot.commands.Tower.EmptyTower;
import frc.robot.commands.Vision.AlignToGoalAuto;
import frc.robot.commands.Vision.AlignToGoalInPlace;
import frc.robot.subsystems.Drive;
import frc.robot.subsystems.Shooter;
import frc.robot.subsystems.Sorter;
import frc.robot.subsystems.Tower;
import frc.robot.tracking.LimelightInterface;

// NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// information, see:
// https://docs.wpilib.org/en/latest/docs/software/commandbased/convenience-features.html
public class ParallelBasicAuto extends SequentialCommandGroup {
  /**
   * Creates a new BasicAuto.
   */
  public ParallelBasicAuto(Drive drive, Shooter shooter, Tower tower, Sorter sorter, LimelightInterface limelight) {
    // Add your commands in the super() call, e.g.
    // super(new FooCommand(), new BarCommand());
    super(
      // new ParallelCommandGroup(

      // new AlignToGoalAuto(drive),

      // new ShooterRPM(shooter, ()->(tower.getBeamBreakFourState() || tower.getBeamBreakThreeState() || tower.getBeamBreakTwoState() || tower.getBeamBreakOneState())),        
      // // ),
      // new EmptyTower(tower, shooter, sorter),
      // new TurnToAngle(180.0, drive, ()->0.0),
      new SequentialCommandGroup(
        new ParallelCommandGroup(
                                  new AlignToGoalAuto(drive), 
                                  new ShooterRPM(
                                    shooter, 
                                    tower,
                                    ()->
                                      (!limelight.alignedToGoal()), false)),

        new ParallelCommandGroup(
                                  new EmptyTower(tower, shooter, sorter),
                                  new ShooterRPM(
                                    shooter, 
                                    tower,
                                    ()->
                                      (tower.getBeamBreakFourState() ||
                                      tower.getBeamBreakThreeState() ||
                                      tower.getBeamBreakTwoState()   || 
                                      tower.getBeamBreakOneState()), false))
        ).withTimeout(13.0),

      new FollowPath(Paths.get("/home/lvuser/deploy/forward4.wpilib.json"), drive, false).getDriveForwardRamseteCommand()
    );
  }
}
