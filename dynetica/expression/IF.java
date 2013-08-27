package dynetica.expression;

public class IF extends ConditionExpression {

    public IF(GeneralExpression[] tempDat) {
        super(tempDat);
        type = ExpressionConstants.IF;
    }

    public void compute() {
        if (data[0].getValue() == 1.0)
            value = data[1].getValue();
        else
            value = 0.0;
    }

    public String toString() {
        return " If(" + data[0].toString() + ") then (" + data[1].toString()
                + ") ";
    }
} // IF
