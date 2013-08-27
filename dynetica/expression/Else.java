package dynetica.expression;

public class Else extends ConditionExpression {

    public Else(GeneralExpression[] tempDat) {
        super(tempDat);
        type = ExpressionConstants.ELSE;
    }

    public void compute() {
        int result = (data.length) % 2;
        int index = 0;
        if (result == 0) {
            while (index < (data.length - 1)) {
                LogicalExpression tempA = (LogicalExpression) data[index];
                index++;
                GeneralExpression tempB = data[index];
                index++;
                if (tempA.getValue() == 1.0) {
                    value = tempB.getValue();
                    index = data.length;
                }
            }
        } else {
            while (index < (data.length - 3)) {
                LogicalExpression tempA = (LogicalExpression) data[index];
                index++;
                GeneralExpression tempB = data[index];
                index++;
                if (tempA.getValue() == 1.0) {
                    value = tempB.getValue();
                    index = data.length;
                }
            }
            if (index < data.length) {
                LogicalExpression tempA = (LogicalExpression) data[index];
                index++;
                GeneralExpression tempB = data[index];
                index++;
                GeneralExpression tempC = data[index];
                if (tempA.getValue() == 1.0) {
                    value = tempB.getValue();
                } else {
                    value = tempC.getValue();
                }
            }
        }

    }

    public String toString() {
        int result = (data.length) % 2;
        int index = 0;
        StringBuffer sb = new StringBuffer("if(");
        if (result == 0) {
            while (index < (data.length - 1)) {
                sb.append(data[index].toString());
                index++;
                sb.append(") { ");
                sb.append(data[index].toString());
                index++;
                sb.append("} else if(");
            }
        } else {
            while (index < (data.length - 3)) {
                sb.append(data[index].toString());
                index++;
                sb.append(") { ");
                sb.append(data[index].toString());
                index++;
                sb.append("} else if(");
            }
            sb.append(data[index].toString());
            index++;
            sb.append(") {");
            sb.append(data[index].toString());
            index++;
            sb.append("} else {");
            sb.append(data[index].toString());
        }
        sb.append("}");
        return sb.toString();
    }
} // Else
