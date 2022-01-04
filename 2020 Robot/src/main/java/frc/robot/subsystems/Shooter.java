/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.TreeMap;

import com.ctre.phoenix.sensors.CANCoder;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class Shooter extends SubsystemBase {
  /**
   * creates a new Shooter
   */
  private static Shooter instance = null;

  public static Shooter getInstance() {
    if (instance == null) {
      instance = new Shooter();
    }
    return instance;
  }

  private CANSparkMax mShooterMotorMaster;
  private CANSparkMax mShooterMotorSlave;

  private CANCoder mShooterEncoder;

  private TreeMap<Double, Double> mRPMTable;
  private double targetCount;
  private int offTargetCount;
  private double rpmThreshold;
  private double rpmSetPoint;
  private File file;

  private static double differenceBetweenRPMAndGoal;
  public Shooter() {
    //Setup the shooter motors (2x Neo)
    mShooterMotorMaster = new CANSparkMax(Constants.Shooter.SHOOTER_MOTOR_PORT_LEFT, MotorType.kBrushless);
    mShooterMotorSlave = new CANSparkMax(Constants.Shooter.SHOOTER_MOTOR_PORT_RIGHT, MotorType.kBrushless);

    // Configure the shooter motor settings
    mShooterMotorMaster.restoreFactoryDefaults();
    mShooterMotorSlave.restoreFactoryDefaults();
    mShooterMotorMaster.setSmartCurrentLimit(Constants.Shooter.SHOOTER_MOTOR_CURRENT_LIMIT);
    mShooterMotorSlave.setSmartCurrentLimit(Constants.Shooter.SHOOTER_MOTOR_CURRENT_LIMIT);
    mShooterMotorMaster.setIdleMode(IdleMode.kCoast);
    mShooterMotorSlave.setIdleMode(IdleMode.kCoast);
    mShooterMotorMaster.setOpenLoopRampRate(0.5);

    // Restrict backwards motion since the shooter should only ever really spin in the desired direction
    mShooterMotorMaster.enableSoftLimit(CANSparkMax.SoftLimitDirection.kReverse, true);
    mShooterMotorMaster.setSoftLimit(CANSparkMax.SoftLimitDirection.kReverse, 0);

    // Set the slave motor to be slaved to the master motor, but inverts it's direction
    mShooterMotorSlave.follow(mShooterMotorMaster, true);
    // mShooterMotorMaster.setInverted(false);
    // mShooterMotorSlave.setInverted(true);

    // Burn the configuration to flash memory to prevent issues if power is lost while operating
    mShooterMotorMaster.burnFlash();
    mShooterMotorSlave.burnFlash();

    // Create the CANCoder object which we'll be using to track the shooter's RPM
    mShooterEncoder = new CANCoder(Constants.Shooter.CAN_ENCODER_PORT);

    // Default some of the internal shooter parameters
    rpmSetPoint = 2000.0;
    rpmThreshold = 100.0;
    targetCount = 0.0;      // Counts how many cycles the shooter RPM has been on target
    offTargetCount = 0;

    //Creation of RPM table
    mRPMTable = new TreeMap<Double, Double>();

    // Get file location
    file = new File("/home/lvuser/deploy/RangeTable.csv");

    try {
      createRPMTable();
    } catch (IOException e) {
      e.printStackTrace();
    }
    
   SmartDashboard.putBoolean("Shooter/Manual RPM Control", false);
  }

  /**
  *sets speed of motors on shooter. 
  *we only have to do this for the first motor as the second is slaved
  */
  public void setSpeed(double motorSpeed) {
    mShooterMotorMaster.set(motorSpeed);
    // mShooterMotorSlave.set(motorSpeed);
  }

  /**
   * Method for getting shooter rpm
   * @return Current shooter RPM
   */
  public double getRPM() {
    return mShooterEncoder.getVelocity() * Constants.Shooter.RPM_COEFFICIENT;
  }
  
  /**
   * @return the difference between the goal rpm and the current
   */
  public double getDifferenceBetweenRPMAndGoal() {
    return differenceBetweenRPMAndGoal;
  }

  /**
   * @param differenceBetweenRPMAndGoal updates the difference between the rpm and the goal
   */
  public void setDifferenceBetweenRPMAndGoal(double differenceBetweenRPMAndGoal) {
    Shooter.differenceBetweenRPMAndGoal = differenceBetweenRPMAndGoal;
  }
  /**
   * Gets whether or not the shooter is close to set rpm
   * @return true = at target rpm
   */
  public boolean getOnRPMTarget() {
    rpmSetPoint = SmartDashboard.getNumber("Shooter/RPM in Command", rpmSetPoint);
    rpmThreshold = SmartDashboard.getNumber("Shooter/RPM Threshold", rpmThreshold);
    return Math.abs(rpmSetPoint - getRPM()) < rpmThreshold;
  }

  /**
   * Gets the count of being on target
   * @return target counter
   */
  public double getOnTargetCount() {
    return targetCount;
  }

  /**
   * Creates the Shooter RPM Table
   * @throws IOException
   */
  public void createRPMTable() throws IOException {
    String line = "";
    BufferedReader csvReader = new BufferedReader(new FileReader(file));
    line = csvReader.readLine();
    while (line != null) {
      String[] temp = line.split(",");
      mRPMTable.put(Double.parseDouble(temp[0]), Double.parseDouble(temp[1]));
      line = csvReader.readLine();
    }
    csvReader.close();
  }

  /**
   * Gets the rpm table
   * @return RPM Table
   */
  public TreeMap<Double, Double> getRPMTable() {
    return mRPMTable;
  }

  /**
   * Gets the calculated RPM based on distance
   * @param range - distance from goal
   * @return RPM for shooter
   */
  public double getRPMFromTable(double range) {
    double lowKey = -1.0;
    double lowVal = -1.0;
    double returnVal = Constants.Shooter.DEFAULT_SHOOTER_RPM;

    for (double key : mRPMTable.keySet()) {
      if (range < key) {
        double highVal = mRPMTable.get(key);
        if (lowKey > 0.0) {
          double m = (range - lowKey) / (key - lowKey);
          returnVal = lowVal + m * (highVal - lowVal);
          break;
        } else {
          returnVal = highVal;
          break;
        }
      }
      lowKey = key;
      lowVal = mRPMTable.get(key);
    }
    return Math.min(returnVal, 6500.0);
  }

  /**
   * returns the primary encoder port of the shooter
   * @return the encoder for the first shooter motor
   */
  public CANEncoder getBuiltInEncoder() {
    return mShooterMotorMaster.getEncoder();
  }

  /**
   * @return gets the value of the rpm threshold
   */
  public double getRPMThreshold() {
    return rpmThreshold;
  }

  /**
   * logs the motor's velocity to the dashboard
   */
  public void logToDashboard() {
    SmartDashboard.putNumber("Shooter/Shooter RPM", getRPM());
    SmartDashboard.putString("Shooter/Get Last Unit String", mShooterEncoder.getLastUnitString());
    
    boolean mIsManual = SmartDashboard.getBoolean("Shooter/Manual RPM Control", false);
    if (!mIsManual){
      SmartDashboard.putNumber("Shooter/RPM SetPoint", rpmSetPoint);
    }
    SmartDashboard.putNumber("Shooter/RPM Threshold", rpmThreshold);
    SmartDashboard.putBoolean("Shooter/In RPM Range", getOnRPMTarget());
    SmartDashboard.putNumber("Shooter/On RPM Count", getOnTargetCount());
  }

  @Override
  public void periodic() {
    
    if (getOnRPMTarget()) {
      targetCount++;
      offTargetCount = 0;
    } else {
      if (offTargetCount > 5){
        targetCount = 0.0;
      } else {
        offTargetCount++;
      }
    }
    logToDashboard();
  }
}
