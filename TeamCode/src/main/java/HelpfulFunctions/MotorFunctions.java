package HelpfulFunctions;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

abstract public class MotorFunctions {
    static public DcMotor initializeMotor(String motorName, HardwareMap hardwareMap) {
        //Initialize the motor by getting the motor object from the Driver Hub.
        DcMotor motor = hardwareMap.dcMotor.get(motorName);
        //Return this motor object to user
        return motor;
    }

}