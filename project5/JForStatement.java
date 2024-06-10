// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import java.util.ArrayList;

import static jminusminus.CLConstants.*;

/**
 * The AST node for a for-statement.
 */
class JForStatement extends JStatement {
    // Initialization.
    private ArrayList<JStatement> init;

    // Test expression
    private JExpression condition;

    // Update.
    private ArrayList<JStatement> update;

    // The body.
    private JStatement body;

    public boolean hasBreak;
    public String breakLabel;
    public boolean hasContinue;
    public String continueLabel;

    /**
     * Constructs an AST node for a for-statement.
     *
     * @param line      line in which the for-statement occurs in the source file.
     * @param init      the initialization.
     * @param condition the test expression.
     * @param update    the update.
     * @param body      the body.
     */
    public JForStatement(int line, ArrayList<JStatement> init, JExpression condition,
                         ArrayList<JStatement> update, JStatement body) {
        super(line);
        this.init = init;
        this.condition = condition;
        this.update = update;
        this.body = body;
    }

    /**
     * {@inheritDoc}
     */
    public JForStatement analyze(Context context) {
        LocalContext localContext = new LocalContext(context);
        JMember.enclosingStatement.push(this); // Push reference
        // Check if empty initial
        if (init != null) {
            for (JStatement statement : init) {
                statement.analyze(localContext);
            }
        }
        // Check if empty condition
        if(condition != null) {
            condition = (JExpression) condition.analyze(localContext);
            condition.type().mustMatchExpected(line(), Type.BOOLEAN);
        }
        // Check if empty update
        if (update != null) {
            for (JStatement statement : update) {
                statement.analyze(localContext);
            }
        }
        body = (JStatement)body.analyze(localContext);
        JMember.enclosingStatement.pop(); // Pop reference
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        if (init != null) {
            for (JStatement statement : init) {
                statement.codegen(output);
            }
        }
        String startLabel = output.createLabel();
        String endLabel = output.createLabel();

        // Go to end if break, go to start if continue
        breakLabel = endLabel;
        continueLabel = startLabel;

        output.addLabel(startLabel);

        if (condition != null)
            condition.codegen(output, endLabel, false);

        if (hasBreak) {
            output.addLabel(breakLabel);
        }

        // Probably a better way of handling this, but when I had a continue label, it would put the update
        // outside of the scope of the loop, so I have to generate the update before the body if there is a continue
        // label
        if (hasContinue) {
            output.addLabel(continueLabel);
            if(update != null) {
                for (JStatement statement : update) {
                    statement.codegen(output);
                }
            }
            body.codegen(output);
        } else {
            body.codegen(output);
            if (update != null) {
                for (JStatement statement : update) {
                    statement.codegen(output);
                }
            }
        }

        output.addBranchInstruction(GOTO, startLabel);
        output.addLabel(endLabel);
    }

    /**
     * {@inheritDoc}
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("JForStatement:" + line, e);
        if (init != null) {
            JSONElement e1 = new JSONElement();
            e.addChild("Init", e1);
            for (JStatement stmt : init) {
                stmt.toJSON(e1);
            }
        }
        if (condition != null) {
            JSONElement e1 = new JSONElement();
            e.addChild("Condition", e1);
            condition.toJSON(e1);
        }
        if (update != null) {
            JSONElement e1 = new JSONElement();
            e.addChild("Update", e1);
            for (JStatement stmt : update) {
                stmt.toJSON(e1);
            }
        }
        if (body != null) {
            JSONElement e1 = new JSONElement();
            e.addChild("Body", e1);
            body.toJSON(e1);
        }
    }
}
