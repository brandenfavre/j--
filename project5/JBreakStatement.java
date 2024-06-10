// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import static jminusminus.CLConstants.*;

/**
 * An AST node for a break-statement.
 */
public class JBreakStatement extends JStatement {
    private JStatement enclosingStatement;
    /**
     * Constructs an AST node for a break-statement.
     *
     * @param line line in which the break-statement occurs in the source file.
     */
    public JBreakStatement(int line) {
        super(line);
    }

    /**
     * {@inheritDoc}
     */
    public JStatement analyze(Context context) {
        enclosingStatement = JMember.enclosingStatement.peek();
        if (enclosingStatement.getClass().equals(JForStatement.class)) {
            ((JForStatement)enclosingStatement).hasBreak = true;
        } else if (enclosingStatement.getClass().equals(JWhileStatement.class)) {
            ((JWhileStatement)enclosingStatement).hasBreak = true;
        } else if (enclosingStatement.getClass().equals(JDoStatement.class)) {
            ((JDoStatement)enclosingStatement).hasBreak = true;
        } else if (enclosingStatement.getClass().equals(JSwitchStatement.class)) {
            ((JSwitchStatement)enclosingStatement).hasBreak = true;
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        if (enclosingStatement.getClass().equals(JForStatement.class)) {
            output.addBranchInstruction(GOTO, ((JForStatement) enclosingStatement).breakLabel);
        } else if (enclosingStatement.getClass().equals(JWhileStatement.class)) {
            output.addBranchInstruction(GOTO, ((JWhileStatement) enclosingStatement).breakLabel);
        } else if (enclosingStatement.getClass().equals(JDoStatement.class)) {
            output.addBranchInstruction(GOTO, ((JDoStatement)enclosingStatement).breakLabel);
        } else if (enclosingStatement.getClass().equals(JSwitchStatement.class)) {
            output.addBranchInstruction(GOTO, ((JSwitchStatement)enclosingStatement).breakLabel);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("JBreakStatement:" + line, e);
    }
}
