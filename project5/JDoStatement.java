// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import static jminusminus.CLConstants.*;

/**
 * The AST node for a do-statement.
 */
public class JDoStatement extends JStatement {
    // Body.
    private JStatement body;

    // Test expression.
    private JExpression condition;

    public boolean hasBreak;
    public String breakLabel;
    public boolean hasContinue;
    public String continueLabel;


    /**
     * Constructs an AST node for a do-statement.
     *
     * @param line      line in which the do-statement occurs in the source file.
     * @param body      the body.
     * @param condition test expression.
     */
    public JDoStatement(int line, JStatement body, JExpression condition) {
        super(line);
        this.body = body;
        this.condition = condition;
    }

    /**
     * {@inheritDoc}
     */
    public JStatement analyze(Context context) {
        JMember.enclosingStatement.push(this); // Push reference
        body = (JStatement)body.analyze(context);
        condition = (JExpression)condition.analyze(context);
        condition.type().mustMatchExpected(line(), Type.BOOLEAN);
        JMember.enclosingStatement.pop(); // Pop reference
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        // Similar to if/condition statement, need to create all labels so no need for if statements
        // and have a startLabel as well for the do
        String elseLabel = output.createLabel();
        String endLabel = output.createLabel();
        String startLabel = output.createLabel();

        breakLabel = elseLabel;
        continueLabel = startLabel;

        output.addLabel(endLabel);

        if (hasBreak) {
            output.addLabel(breakLabel);
        }
        if (hasContinue) {
            output.addLabel(continueLabel);
        }

        output.addLabel(startLabel);
        body.codegen(output);

        condition.codegen(output, elseLabel, false);
        output.addBranchInstruction(GOTO, endLabel);
        output.addLabel(elseLabel);
    }

    /**
     * {@inheritDoc}
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("JDoStatement:" + line, e);
        JSONElement e1 = new JSONElement();
        e.addChild("Body", e1);
        body.toJSON(e1);
        JSONElement e2 = new JSONElement();
        e.addChild("Condition", e2);
        condition.toJSON(e2);
    }
}
