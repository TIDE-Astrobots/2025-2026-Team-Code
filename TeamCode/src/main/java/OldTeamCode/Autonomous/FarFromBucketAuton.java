package OldTeamCode.Autonomous;//import com.acmerobotics.dashboard.FtcDashboard;
//import com.acmerobotics.dashboard.config.Config;
//import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.hardware.kauailabs.NavxMicroNavigationSensor;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.IntegratingGyroscope;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

import HelpfulFunctions.Dijkstra.*;

import java.util.List;


/*
Some important notes:
- The claw servo starts at 0 here and 1 on the tournament opMode for some reason
 */
//@Config
@Autonomous(name = "Far From Bucket V0.0.8")
public class FarFromBucketAuton extends LinearOpMode {
    //region: Creating Variables
    //these variables correspond to servos and motors. They are displayed in order of distance to Control Hub.
    private DcMotor WheelMotorLeftFront;
    private DcMotor WheelMotorLeftBack;
    private DcMotor WheelMotorRightBack;
    private DcMotor WheelMotorRightFront;
    private DcMotor chainLeft;
    private DcMotor chainRight;
    private DcMotor extendoLeft;
    private DcMotor extendoRight;
    private DcMotor[] WheelMotors;
    private float ticksPerRevolution;
    private float wheelCircumference;
    private float wheelSideLength;
    private float dist;
    private float rot;
    private int task;
    private int target;
    private Field gameField;
    private int taskNumber;
    private boolean runOnce;
    private DcMotor armPivot;
    private Servo clawServo;
    private Servo otherClawServo;
    IntegratingGyroscope gyro;
    NavxMicroNavigationSensor navxMicro;
    //endregion

    @Override
    public void runOpMode() throws InterruptedException {
        //region: Initializing Variables
        //These variables do NOT correspond to a physical object; they are entirely digital and for coding purposes.
//        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        float speedMultipler = 0.4f;
        ticksPerRevolution = 537.7f;
        wheelCircumference = 11.8737f;
        wheelSideLength = 1.486f;
        dist = -36f;
        rot = 180f;
        task = 0;
        target = Integer.MAX_VALUE;
        gameField = new Field("3-1");
        taskNumber = 0;
        runOnce = true;
        clawServo = hardwareMap.servo.get("clawServo");
        clawServo.setPosition(1);
        otherClawServo = hardwareMap.servo.get("otherClawServo");
        otherClawServo.setPosition(0);


        //This section maps the variables to their corresponding motors/servos
        WheelMotorLeftFront = HelpfulFunctions.MotorFunctions.initializeMotor("WheelMotorLeftFront", hardwareMap);
        WheelMotorRightFront = HelpfulFunctions.MotorFunctions.initializeMotor("WheelMotorRightFront", hardwareMap);
        WheelMotorLeftBack = HelpfulFunctions.MotorFunctions.initializeMotor("WheelMotorLeftBack", hardwareMap);
        WheelMotorRightBack = HelpfulFunctions.MotorFunctions.initializeMotor("WheelMotorRightBack", hardwareMap);
        WheelMotorRightFront.setDirection(DcMotorSimple.Direction.REVERSE);
        WheelMotorRightBack.setDirection(DcMotorSimple.Direction.REVERSE);
        //This section creates an array of motors that will make some later code easier
        WheelMotors = new DcMotor[4];
        WheelMotors[0] = WheelMotorLeftFront;
        WheelMotors[1] = WheelMotorRightFront;
        WheelMotors[2] = WheelMotorLeftBack;
        WheelMotors[3] = WheelMotorRightBack;
        WheelMotors[0].setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        WheelMotors[1].setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        WheelMotors[2].setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        WheelMotors[3].setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        WheelMotors[0].setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        WheelMotors[1].setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        WheelMotors[2].setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        WheelMotors[3].setMode(DcMotor.RunMode.RUN_USING_ENCODER);


        //This section initializes the motors that control the extension arms and sets their settings
        extendoLeft = hardwareMap.dcMotor.get("extendoLeft");
        extendoRight = hardwareMap.dcMotor.get("extendoRight");
        extendoRight.setDirection(DcMotorSimple.Direction.REVERSE);
        extendoLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        extendoRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        extendoLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        extendoRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        extendoLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        extendoRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        armPivot = hardwareMap.dcMotor.get("armPivot");
        armPivot.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        armPivot.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        armPivot.setDirection(DcMotorSimple.Direction.REVERSE);
        //endregion

        ElapsedTime timer = new ElapsedTime();
        navxMicro = hardwareMap.get(NavxMicroNavigationSensor.class, "navx");
        gyro = (IntegratingGyroscope)navxMicro;

        // The gyro automatically starts calibrating. This takes a few seconds.
        telemetry.log().add("Gyro Calibrating. Do Not Move!");

        // Wait until the gyro calibration is complete
        timer.reset();
        while (navxMicro.isCalibrating())  {
            telemetry.addData("calibrating", "%s", Math.round(timer.seconds())%2==0 ? "|.." : "..|");
            telemetry.update();
            Thread.sleep(50);
        }
        telemetry.log().clear(); telemetry.log().add("Gyro Calibrated. Press Start.");
        telemetry.clear(); telemetry.update();

        //Wait for the user to press start
        waitForStart();
        //called continuously while OpMode is active

        while(opModeIsActive()) {
            /*
            Measurements:
            Circumference = 3.780"
            float ticksPerRevolution = ((((1+(46/17))) * (1+(46/11))) * 28);
            ticksPerRevolution = 537.7
             */
            //1.
            List<List<String>> steps = gameField.getInstructionsList("3-1", "4-2");
            telemetry.addData("STEPS", steps);
            telemetry.update();
            while(true) {
                if(runOnce) {
                    runOnce = false;
                    List<String> taskList;
                    try {
                        taskList = steps.get(taskNumber);
                    }
                    catch(Exception e) {
                        break;
                    }
                    moveDirectionInInches(taskList.get(0), 24);
                    target = WheelMotorLeftFront.getTargetPosition();
                }

                telemetry.addData("Task number:", taskNumber);
                telemetry.update();

                if(WheelMotorLeftFront.getCurrentPosition() == target) {
                    runOnce = true;
                    taskNumber += 1;
                    target = Integer.MAX_VALUE;
                }
            }

            //2.ROTATE 90 degrees counterclockwise
            resetTask();
            while(true)
            {
                if(runOnce) {
                    runOnce = false;
                    rotateToAngle(-90);
                }

                taskNumber += 1;
                break;
            }

            //3 RAISE ARM
            resetTask();
            while(true) {
                if(runOnce) {
                    runOnce = false;
//                    extendoLeft.setTargetPosition(3000);
//                    extendoRight.setTargetPosition(3000);
                    extendoLeft.setTargetPosition(750);
                    extendoRight.setTargetPosition(750);
                    extendoLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    extendoRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    extendoLeft.setPower(1);
                    extendoRight.setPower(1);
                }
                telemetry.addData("Task number:", taskNumber);
                telemetry.addData("currpos:", extendoLeft.getCurrentPosition());
                telemetry.addData("otherpos:", extendoRight.getCurrentPosition());
                telemetry.update();
                if(extendoLeft.getTargetPosition() < -extendoLeft.getCurrentPosition()) {
                    extendoLeft.setPower(0);
                    extendoRight.setPower(0);
                    taskNumber += 1;
                    break;
                }
            }


            //4. PIVOT ARM DOWN
            resetTask();
            while(true) {
                if(runOnce) {
                    runOnce = false;
                    armPivot.setTargetPosition(-130);
                    armPivot.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    armPivot.setPower(0.75);
                }
                if(armPivot.getCurrentPosition() < -120) {
//                    armPivot.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                    armPivot.setPower(0.075);
                }
                telemetry.addData("Task number:", taskNumber);
                telemetry.addData("Power", armPivot.getPower());
                telemetry.addData("RunModed", armPivot.getMode());
                telemetry.addData("Position:", armPivot.getCurrentPosition());
                telemetry.addData("Target: ", armPivot.getTargetPosition());
                telemetry.update();

                if(armPivot.getCurrentPosition() <= armPivot.getTargetPosition()) {
                    taskNumber += 1;
                    break;
                }
            }

            armPivot.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            armPivot.setPower(-0.6);
//            extendoLeft.setDirection(extendoLeft.getDirection().inverted());
//            extendoRight.setDirection(extendoRight.getDirection().inverted());
            extendoLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            extendoRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            extendoLeft.setPower(-0.5);
            extendoRight.setPower(-0.5);
            clawServo.setPosition(0.9);
            otherClawServo.setPosition(0.1);
            Thread.sleep(3000);



            //5 MOVE BACKWARDS
            resetTask();
            while(true) {
                if(runOnce) {
                    runOnce = false;
                    moveDirectionInInches("back", 6f);

                }

                telemetry.addData("Task number:", taskNumber);
                telemetry.update();

                if(WheelMotorLeftFront.getTargetPosition() == WheelMotorLeftFront.getCurrentPosition()) {
                    taskNumber += 1;
                    break;
                }
            }


            /*
            1. Move from 3-1 to 4-2
             */
        }
    }

    public void resetTask() {
        target = Integer.MAX_VALUE;
        runOnce = true;
    }
    public void moveDirectionInInches(String direction, float distance) {
        if(direction == "right") {
            moveDistanceInInchesRight(distance);
        }
        if(direction == "left") {
            moveDistanceInInchesRight(-distance);
        }
        if(direction == "forward") {
            moveDistanceInInches(distance);
        }
        if(direction == "back") {
            moveDistanceInInches(-distance);
        }
    }

    public void rotateToAngle(float degrees) {
        for(DcMotor motor : WheelMotors) {
            motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }

        Orientation angles = gyro.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);

        double power = 0.25;
        if(degrees < angles.firstAngle) {
            power = -power;
        }
        while(Math.round(AngleUnit.DEGREES.normalize(angles.firstAngle)) > degrees + 1 || Math.round(AngleUnit.DEGREES.normalize(angles.firstAngle)) < degrees - 1) {
            telemetry.addData("navx pos Z", AngleUnit.DEGREES.normalize(angles.firstAngle));
            telemetry.addData("navx pos Y", AngleUnit.DEGREES.normalize(angles.secondAngle));
            telemetry.addData("navx pos X", AngleUnit.DEGREES.normalize(angles.thirdAngle));


            WheelMotorLeftFront.setPower(power);
            WheelMotorLeftBack.setPower(power);
            WheelMotorRightBack.setPower(-power);
            WheelMotorRightFront.setPower(-power);
            angles = gyro.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
            telemetry.addData("power", power);
            telemetry.update();
        }
        WheelMotorLeftFront.setPower(0);
        WheelMotorLeftBack.setPower(0);
        WheelMotorRightBack.setPower(0);
        WheelMotorRightFront.setPower(0);

    }

    //    public void rotateInDegrees(float degrees)
//    {
//        /*
//        Rotates in place n degrees clockwise. Use negative values for counterclockwise.
//         */
//        int magnitude = (int) Math.round(4600 * Math.sin((Math.PI/180) * degrees/6.325));
//        WheelMotorLeftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        WheelMotorLeftFront.setTargetPosition(magnitude);
//        WheelMotorLeftFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//        target = magnitude;
//
//        WheelMotorLeftBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        WheelMotorLeftBack.setTargetPosition(magnitude);
//        WheelMotorLeftBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//
//        WheelMotorRightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        WheelMotorRightFront.setTargetPosition(-magnitude);
//        WheelMotorRightFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//
//        WheelMotorRightBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        WheelMotorRightBack.setTargetPosition(-magnitude);
//        WheelMotorRightBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//        for(DcMotor motor : WheelMotors) {
//            motor.setPower(0.25);
//        }
//    }
    public void moveDistanceInInchesRight(float distance) {
        /*
        Moves a float distance of inches right. Use negative values for left.
         */

        int magnitude = Math.round((distance/wheelSideLength) * (ticksPerRevolution * (wheelSideLength/wheelCircumference)));
        WheelMotorLeftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        WheelMotorLeftFront.setTargetPosition(magnitude);
        WheelMotorLeftFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        target = magnitude;

        WheelMotorLeftBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        WheelMotorLeftBack.setTargetPosition(-magnitude);
        WheelMotorLeftBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        WheelMotorRightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        WheelMotorRightFront.setTargetPosition(-magnitude);
        WheelMotorRightFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        WheelMotorRightBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        WheelMotorRightBack.setTargetPosition(magnitude);
        WheelMotorRightBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        for(DcMotor motor : WheelMotors) {
            motor.setPower(0.25);
        }
    }
    public void moveDistanceInInches(float distance) {
        /*
        Moves distance inches forward. Use negative values for backward.
         */
        for(DcMotor motor : WheelMotors) {
            motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motor.setTargetPosition(-Math.round((distance/wheelCircumference) * ticksPerRevolution));
            motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        }
        target = -Math.round((distance/wheelCircumference) * ticksPerRevolution);
        for(DcMotor motor: WheelMotors) {
            motor.setPower(0.5);
        }
    }

    public void moveRobotInDirection(String direction, float speedMultipler) {
        if (direction == "forward") {
            WheelMotorLeftFront.setPower(-1 * speedMultipler);
            WheelMotorRightFront.setPower(1 * speedMultipler);
            WheelMotorLeftBack.setPower(-1 * speedMultipler);
            WheelMotorRightBack.setPower(1 * speedMultipler);
        } else if (direction == "backward") {
            WheelMotorLeftFront.setPower(1 * speedMultipler);
            WheelMotorRightFront.setPower(-1 * speedMultipler);
            WheelMotorLeftBack.setPower(1 * speedMultipler);
            WheelMotorRightBack.setPower(-1 * speedMultipler);
        } else if (direction == "left") {
            WheelMotorLeftFront.setPower(1 * speedMultipler);
            WheelMotorRightFront.setPower(1 * speedMultipler);
            WheelMotorLeftBack.setPower(1 * speedMultipler);
            WheelMotorRightBack.setPower(1 * speedMultipler);
        } else if (direction == "right") {
            WheelMotorLeftFront.setPower(-1 * speedMultipler);
            WheelMotorRightFront.setPower(-1 * speedMultipler);
            WheelMotorLeftBack.setPower(-1 * speedMultipler);
            WheelMotorRightBack.setPower(-1 * speedMultipler);
        }
    }

    public void stopRobot() {
        WheelMotorLeftFront.setPower(0);
        WheelMotorRightFront.setPower(0);
        WheelMotorLeftBack.setPower(0);
        WheelMotorRightBack.setPower(0);
    }
}