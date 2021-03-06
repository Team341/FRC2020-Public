/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.Autonomous;

import java.nio.file.Path;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.commands.Drive.ResetOdometry;
import frc.robot.subsystems.Drive;

// NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// information, see:
// https://docs.wpilib.org/en/latest/docs/software/commandbased/convenience-features.html
public class TravelPath extends SequentialCommandGroup {
  /**
   * Creates a new TravelPath.
   */
  public TravelPath(Path path, Drive drive, boolean reversed) {
    // Add your commands in the super() call, e.g.
    // super(new FooCommand(), new BarCommand());
    super(
        new ResetOdometry(drive),
        new FollowPath(path, drive, reversed).getDriveForwardRamseteCommand(),
        new InstantCommand(() -> System.out.println("Ended travel path" + path.getFileName()))
    );
  }
}
