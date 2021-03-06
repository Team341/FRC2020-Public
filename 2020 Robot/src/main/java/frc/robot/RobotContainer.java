/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystems.Shooter;
import frc.robot.subsystems.Sorter;
import frc.robot.utilities.XboxController;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import frc.robot.commands.Autonomous.BasicAuto;
import frc.robot.commands.Autonomous.CenterAuto;
import frc.robot.commands.Autonomous.FollowPath;
import frc.robot.commands.Autonomous.LeftSideAuto;
import frc.robot.commands.Autonomous.ParallelBasicAuto;
import frc.robot.commands.Autonomous.RightSideAuto;
import frc.robot.commands.Autonomous.TravelPath;
import frc.robot.commands.ColorWheel.PositionControl;
// import frc.robot.commands.ColorWheel.PositionControl;
import frc.robot.commands.ColorWheel.RotationControl;
import frc.robot.commands.ColorWheel.RunColorWheel;
import frc.robot.commands.ColorWheel.ToggleControlWheelState;
import frc.robot.commands.Drive.DeployPoppers;
import frc.robot.commands.Drive.DriveArcadeMode;
import frc.robot.commands.Drive.ResetOdometry;
import frc.robot.commands.Drive.RetractPoppers;
import frc.robot.commands.Drive.Turn180;
import frc.robot.commands.Drive.TurnToAngleProfiled;
import frc.robot.commands.FloorIntake.LowerFloorIntake;
import frc.robot.commands.FloorIntake.RaiseFloorIntake;
import frc.robot.commands.FloorIntake.RunFloorIntake;
import frc.robot.commands.FloorIntake.RunFloorIntakeByJoystick;
import frc.robot.commands.FloorIntake.ToggleFloorIntakeState;
import frc.robot.commands.Hanger.ElevatorGoToRatioHeight;
import frc.robot.commands.Hanger.RunHanger;
import frc.robot.commands.Hanger.RunWinch;
import frc.robot.commands.Hanger.StopWinch;
import frc.robot.commands.Hanger.UnravelWinch;
import frc.robot.commands.Hanger.goToElevatorPosition;
import frc.robot.commands.MultiSubsystemCommandGroups.AlignToControlWheel;
import frc.robot.commands.MultiSubsystemCommandGroups.RunBallPath;
import frc.robot.commands.MultiSubsystemCommandGroups.RunBallPathByJoystick;
import frc.robot.commands.MultiSubsystemCommandGroups.RunBallPathOut;
import frc.robot.commands.MultiSubsystemCommandGroups.Shoot;
import frc.robot.commands.Shooter.RunShooter;
import frc.robot.commands.Shooter.SetShooterSpeed;
import frc.robot.commands.Shooter.ShooterRPM;
import frc.robot.commands.Sorter.RunSorter;
import frc.robot.commands.Sorter.RunSorterByJoystick;
import frc.robot.commands.Tower.FireBall;
import frc.robot.commands.Tower.RunTowerByJoystick;
import frc.robot.commands.Tower.RunTowerTrigger;
import frc.robot.commands.Tower.runTowerNoSensors;
import frc.robot.commands.Vision.AlignToGoal;
import frc.robot.commands.Vision.SetLimelightLED;
import frc.robot.subsystems.ControlWheel;
import frc.robot.subsystems.Drive;
import frc.robot.subsystems.FloorIntake;
import frc.robot.subsystems.Hanger;
import frc.robot.subsystems.LEDs;
import frc.robot.subsystems.PDPLogger;
// import frc.robot.subsystems.LEDs;
import frc.robot.subsystems.Tower;
import frc.robot.tracking.LimelightInterface;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.Trigger;

import java.nio.file.Paths;

import edu.wpi.first.wpilibj.DriverStation;

/**
 * This class is where the bulk of the robot should be declared. Since
 * Command-based is a "declarative" paradigm, very little robot logic should
 * actually be handled in the {@link Robot} periodic methods (other than the
 * scheduler calls). Instead, the structure of the robot (including subsystems,
 * commands, and button mappings) should be declared here.
 */
public class RobotContainer {

  private static RobotContainer robotContainerInstance;

  private CommandScheduler mCommandScheduler;

  public static XboxController mDriverController;
  public static XboxController mOperatorController;

  private SendableChooser<Command> mDriveType;

  public static RobotContainer getInstance() {
    if (robotContainerInstance == null) {
      robotContainerInstance = new RobotContainer();
    }
    return robotContainerInstance;
  }

  /**
   * The container for the robot. Contains subsystems, OI devices, and commands.
   */

  // subsystem declarations
  private Tower mTower;
  public Drive mDrive;
  private FloorIntake mFloorIntake;
  // private LEDs mLEDs;
  private Shooter mShooter;
  private Sorter mSorter;
  private Hanger mHanger;
  private LimelightInterface mLimelightInterface;
  private ControlWheel mControlWheel;
  private final PDPLogger mPDP;
  private LEDs mLEDs;

  // define the autonomous chooser
  private SendableChooser<Command> mAutonomousChooser;

  private DriveArcadeMode mDriveArcadeModeCommand;
  // private FollowPath mFollowPath;

  public TravelPath testPath1, testPath2, testPath3, testPath4;

  public RobotContainer() {
    // Instantiate subsystems
    mTower = Tower.getInstance();
    mDrive = Drive.getInstance();
    mFloorIntake = FloorIntake.getInstance();
    mShooter = Shooter.getInstance();
    mHanger = Hanger.getInstance();
    mSorter = Sorter.getInstance();
    mControlWheel = ControlWheel.getInstance();
    mLimelightInterface = LimelightInterface.getInstance();
    mPDP = new PDPLogger();
    mLEDs = new LEDs(mTower, mDrive, mShooter, mHanger, mControlWheel, mLimelightInterface, mPDP);

    // mLimelightInterface.setLimeLightLED(1);
    mCommandScheduler = CommandScheduler.getInstance();

    // Configure the button on the driver & operator controllers
    configureButtonBindings();

    // Create the possible auton commands
    testPath1 = new TravelPath(Paths.get("/home/lvuser/deploy/straight.wpilib.json"), mDrive, true);
    testPath2 = new TravelPath(Paths.get("/home/lvuser/deploy/straight.wpilib.json"), mDrive, false);
    testPath3 = new TravelPath(Paths.get("/home/lvuser/deploy/CurvedPath.wpilib.json"), mDrive, false);
    testPath4 = new TravelPath(Paths.get("/home/lvuser/deploy/InvertedCurvedPath.wpilib.json"), mDrive, true);

    // Declare autonomous chooser
    mAutonomousChooser = new SendableChooser<Command>();
    // mAutonomousChooser.addOption("Basic Auto", new BasicAuto(mDrive, mShooter, mTower, mSorter));
    mAutonomousChooser.addOption("Right Side Auto", new RightSideAuto(mDrive, mShooter, mTower, mSorter, mFloorIntake, mLimelightInterface));
    mAutonomousChooser.addOption("Center Auto", new CenterAuto(mDrive, mShooter, mTower, mSorter, mFloorIntake, mLimelightInterface));
    mAutonomousChooser.addOption("Left Side Auto", new LeftSideAuto(mDrive, mShooter, mTower, mSorter, mFloorIntake, mLimelightInterface));

    mAutonomousChooser.addOption("Forward Off Line", testPath2);
    mAutonomousChooser.addOption("Backwards Off Line", testPath1);
    mAutonomousChooser.addOption("TEST: Forward 90deg Right Turn", testPath3);
    mAutonomousChooser.addOption("TEST: Reverse 90deg Left Turn", testPath4);
    mAutonomousChooser.setDefaultOption("Parallel Basic Auto", new ParallelBasicAuto(mDrive, mShooter, mTower, mSorter, mLimelightInterface));
    Shuffleboard.getTab("Autonomous").add(mAutonomousChooser);

    // Default the drive to be arcade mode 
    mDriveArcadeModeCommand = new DriveArcadeMode(mDrive,
        () -> -1.0 * mDriverController.getDeadbandedLeftYAxis(Constants.XboxController.DEAD_BAND),
        () -> mDriverController.getDeadbandedRightXAxis(Constants.XboxController.DEAD_BAND));
    mDrive.setDefaultCommand(mDriveArcadeModeCommand);


    // // TODO: NO, this shouldn't be done, anything that needs to be continuously run (i.e. checking/setting status/state, should be done in the execute)
    // mShooter.setDefaultCommand(new RunShooter(mShooter, 
    //     () -> -1.0 * mOperatorController.getDeadbandedLeftYAxis(Constants.XboxController.DEAD_BAND)));

    // Default to hanger to the operator's left Y axis        
    mHanger.setDefaultCommand(new RunHanger(mHanger, 
        () -> -1.0 * mOperatorController.getDeadbandedLeftYAxis(Constants.XboxController.HANGER_DEAD_BAND)));


    // Default the intake, sorter & tower to run on the operators's righty Y axis
    mFloorIntake.setDefaultCommand(
        new RunFloorIntakeByJoystick(
            mFloorIntake,
            () -> -1.0 * mOperatorController.getDeadbandedRightYAxis(0.75))
    );

    mSorter.setDefaultCommand(
        new RunSorterByJoystick(
            mSorter,
            () -> -1.0 * mOperatorController.getDeadbandedRightYAxis(0.75))
    );

    mTower.setDefaultCommand(
        new RunTowerByJoystick(
          mTower,
            () -> -1.0 * mOperatorController.getDeadbandedRightYAxis(0.75))
    );

    mControlWheel.setDefaultCommand(
        new RunColorWheel(
          mControlWheel, 
          () -> mOperatorController.getDeadbandedRightXAxis(0.5))
    );

    // mLimelightInterface.setDefaultCommand(new SetLimelightLED(mLimelightInterface, 1));
  }


  

  /**
   * Use this method to define your button->command mappings. Buttons can be
   * created by instantiating a {@link GenericHID} or one of its subclasses
   * ({@link edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then
   * passing it to a {@link edu.wpi.first.wpilibj2.command.button.JoystickButton}.
   */
  private void configureButtonBindings() {
    mDriverController = new XboxController(Constants.XboxController.DRIVER_PORT);
    mOperatorController = new XboxController(Constants.XboxController.OPERATOR_PORT);

    // Driver Controls
    //    Left Stick Y - Drive Speed
    //    Right Stick X - Turn Speed
    //    Left Trigger - Auto Aim
    //    Right Trigger - Shoot
    //    Left Bumper - Turn On Limelight
    //    Right Bumper - Go Half Speed
    //    Back Button - Unspool Left Winch
    //    Start Button - Unspool Right Winch
    //    A Button - Blink Limelight
    //    B Button - 
    //    X Button - Test Profiled Turn 180
    //    Y Button - Align to Color Wheel

    Trigger leftDriverTrigger = new Trigger(() -> mDriverController.getLeftTrigger());
    Trigger rightDriverTrigger = new Trigger(() -> mDriverController.getRightTrigger());

    leftDriverTrigger.whenActive(new ParallelCommandGroup(new AlignToGoal(mDrive, 
                            () -> -1.0 * mDriverController.getDeadbandedLeftYAxis(Constants.XboxController.DEAD_BAND),
                            () -> mDriverController.getDeadbandedRightXAxis(Constants.XboxController.DEAD_BAND),
                            () -> mDriverController.getLeftTrigger()
                            ),
                            new ShooterRPM(mShooter, mTower, () -> mDriverController.getLeftTrigger(), true),
                            new SetLimelightLED(mLimelightInterface, 3)));
    // leftDriverTrigger.whenInactive(new ParallelCommandGroup(new AlignToGoal(mDrive, 
    //                         () -> -1.0 * mDriverController.getDeadbandedLeftYAxis(Constants.XboxController.DEAD_BAND),
    //                         () -> mDriverController.getDeadbandedRightXAxis(Constants.XboxController.DEAD_BAND),
    //                         () -> false
    //                         ),
    //                         new ShooterRPM(mShooter, () -> false), 
    //                         new SetLimelightLED(mLimelightInterface, 1)));

    rightDriverTrigger.whenActive(new FireBall(mTower, mShooter, mSorter, () -> mDriverController.getRightTrigger()));
    rightDriverTrigger.whenInactive(new FireBall(mTower, mShooter, mSorter, () -> false));

    // mDriverController.BumperRight.whenPressed(new Turn180(mDrive, 
    //                               () -> mDriverController.getDeadbandedRightXAxis(Constants.XboxController.DEAD_BAND)));
    // mDriverController.BumperRight.whenReleased(new RetractPoppers(mDrive));

    // mDriverController.BumperLeft.whenPressed(new DeployPoppers(mDrive));
    // mDriverController.BumperLeft.whenReleased(new RetractPoppers(mDrive));

    mDriverController.BumperLeft.whenPressed(new SetLimelightLED(mLimelightInterface, 3));
    mDriverController.BumperLeft.whenReleased(new SetLimelightLED(mLimelightInterface, 1));

    // Drive at half speed, since the inputs are cubed, use 0.8 to get an output of ~0.5
    mDriverController.BumperRight.whenHeld(new DriveArcadeMode(mDrive,
          () -> -0.8 * mDriverController.getDeadbandedLeftYAxis(Constants.XboxController.DEAD_BAND),
          () -> 0.8 * mDriverController.getDeadbandedRightXAxis(Constants.XboxController.DEAD_BAND)));

    mDriverController.ButtonA.whenPressed(new SetLimelightLED(mLimelightInterface, 2));
    mDriverController.ButtonA.whenReleased(new SetLimelightLED(mLimelightInterface, 1));

    mDriverController.ButtonBack.whenHeld(new UnravelWinch(mHanger, true));
    mDriverController.ButtonBack.whenReleased(new StopWinch(mHanger));

    mDriverController.ButtonStart.whenHeld(new UnravelWinch(mHanger, false));
    mDriverController.ButtonStart.whenReleased(new StopWinch(mHanger));
    mDriverController.ButtonY.whenPressed(new AlignToControlWheel(mDrive, mControlWheel, () -> mDriverController.getLeftYAxis()));

    // mDriverController.ButtonY.whenPressed(new AlignToControlWheel(mDrive, mControlWheel, () -> mDriverController.getLeftYAxis()));

    mDriverController.ButtonX.whenPressed(() -> mDrive.resetOdometry());

    mDriverController.ButtonY.whenHeld(new TurnToAngleProfiled(180.0, mDrive));

    // Operator Controls
    //    Left Stick Y - Manual elevator height control
    //    Start Button - Toggle Elevator Preset Heights
    //    Right Stick Y - Manual Ball Path
    //    Right Stick X - Manual control wheel spinner
    //    Left Trigger - Spin up shooter to RPM
    //    Right Trigger - Shoot
    //    Left Bumper - Deploy Control Wheel Device
    //    Right Bumper -  Extend intake, (Toggles state when pressed)
    //    Back Button - Winch Robot Up
    //    Start Button - 
    //    A Button - Intake / sort balls
    //    B Button - Spit Balls
    //    X Button - Control Wheel precision control
    //    Y Button - Spin Control Wheel 3x

    
    Trigger leftOperatorTrigger = new Trigger(() -> mOperatorController.getLeftTrigger());
    Trigger rightOperatorTrigger = new Trigger(() -> mOperatorController.getRightTrigger());

    // leftOperatorTrigger.whenActive(new ShooterRPM(mShooter, mOperatorController.getLeftTrigger()));
    leftOperatorTrigger.whenActive(new ShooterRPM(mShooter, mTower, () -> mOperatorController.getLeftTrigger(), false));
    leftOperatorTrigger.whenInactive(new ShooterRPM(mShooter, mTower, () -> false, false));

    rightOperatorTrigger.whenActive(new FireBall(mTower, mShooter, mSorter, () -> mOperatorController.getRightTrigger()));
    rightOperatorTrigger.whenInactive(new FireBall(mTower, mShooter, mSorter, () -> false));

    mOperatorController.ButtonBack.whenPressed(new goToElevatorPosition(mHanger));

    mOperatorController.BumperLeft.whenPressed(new ToggleControlWheelState(mControlWheel, mHanger));
    mOperatorController.BumperRight.whenPressed(new ToggleFloorIntakeState(mFloorIntake));

    // mOperatorController.BumperRight.whenPressed(new LowerFloorIntake(mFloorIntake));
    // mOperatorController.BumperLeft.whenPressed(new RaiseFloorIntake(mFloorIntake));

    // Winching robot up
    mOperatorController.ButtonStart.whenPressed(new SequentialCommandGroup(new ElevatorGoToRatioHeight(mHanger), new RunWinch(mHanger)));
    mOperatorController.ButtonStart.whenReleased(new StopWinch(mHanger));


    // Intake in/out control
    mOperatorController.ButtonA.whenHeld(new RunBallPath(mFloorIntake, mSorter, mTower));
    mOperatorController.ButtonB.whenHeld(new RunBallPathOut(mFloorIntake, mSorter, mTower));
    // Position Control
    mOperatorController.ButtonX.whenHeld(new PositionControl(mControlWheel));

    // I'm not certain how to get the color from the field message, but it is unlikely to matter
    // And it's throwing erros so I've commented it out for now. 
    // mOperatorController.ButtonX.whenHeld(new PositionControl(mControlWheel.getSmartDashboardColor(), mControlWheel));
    // Rotation Control

    mOperatorController.ButtonY.whenHeld(new RotationControl(mControlWheel));

  }

  public LEDs getLEDs() {
    return mLEDs;
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // An ExampleCommand will run in autonomous
    // return mAutonomousChooser.getSelected();
    // return testPath1.getDriveForwardRamseteCommand();
    // return null;
    // return new BasicAuto(mDrive, mShooter, mTower, mSorter);
    return mAutonomousChooser.getSelected();
    // return new FollowPath(Paths.get("/home/lvuser/deploy/RightTrenchRun.wpilib.json"), mDrive, false).getDriveForwardRamseteCommand();
  }
  
  public static double applyDeadband(double value, double deadband) {
    if (value > -deadband && value < deadband) {
      return 0.0;
    } else {
      return value;
    }
  }
}