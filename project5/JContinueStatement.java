// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import static jminusminus.CLConstants.GOTO;

/**
 * An AST node for a continue-statement.
 */
public class JContinueStatement extends JStatement {
    private JStatement enclosingStatement;
    /**
     * Constructs an AST node for a continue-statement.
     *
     * @param line line in which the continue-statement occurs in the source file.
     */
    public JContinueStatement(int line) {
        super(line);
    }

    /**
     * {@inheritDoc}
     */
    public JStatement analyze(Context context) {
        enclosingStatement = JMember.enclosingStatement.peek();
        if (enclosingStatement.getClass().equals(JForStatement.class)) {
            System.out.println("In for CONTINUE");
            ((JForStatement)enclosingStatement).hasContinue = true;
        } else if (enclosingStatement.getClass().equals(JWhileStatement.class)) {
            System.out.println("In while CONTINUE");
            ((JWhileStatement)enclosingStatement).hasContinue = true;
        } else if (enclosingStatement.getClass().equals(JDoStatement.class)) {
            System.out.println("In do CONTINUE");
            ((JDoStatement)enclosingStatement).hasContinue = true;
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        //String label = ((JForStatement)enclosingStatement).continueLabel;
        if (enclosingStatement.getClass().equals(JForStatement.class)) {
            output.addBranchInstruction(GOTO, ((JForStatement) enclosingStatement).continueLabel);
        } else if (enclosingStatement.getClass().equals(JWhileStatement.class)) {
            output.addBranchInstruction(GOTO, ((JWhileStatement) enclosingStatement).continueLabel);
        } else if (enclosingStatement.getClass().equals(JDoStatement.class)) {
            output.addBranchInstruction(GOTO, ((JDoStatement) enclosingStatement).continueLabel);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("JContinueStatement:" + line, e);
    }
}
