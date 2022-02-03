package gui;


import data_classes.Program;

import javax.swing.*;

public class UpdateProgram extends JDialog{
    private JPanel mainPanel;
    private JTextField programNameInput;
    private JTextField programIDInput;
    private JRadioButton UNDERGRADUATERadioButton;
    private JRadioButton POSTGRADUATERadioButton;
    private JButton saveButton;
    private JButton cancelButton;
    private String name, pid, type;
    private Program program;
    private Boolean flag = false;


    /*
    * Custom dialog box to edit programs.
    * */
    public UpdateProgram(Program program) {

        setTitle("Editing Program :"+program.getName());
        setModal(true);
        getRootPane().setDefaultButton(saveButton);

        this.pid = program.getPid();
        this.name = program.getName();
        this.type = program.getType();

        programNameInput.setText(name);
        programIDInput.setText(pid);
        if(type.equalsIgnoreCase("POSTGRADUATE")){
            POSTGRADUATERadioButton.setSelected(true);
        }else{
            UNDERGRADUATERadioButton.setSelected(true);
        }

        saveButton.addActionListener( e -> {

            if(programNameInput.getText().isBlank() || programIDInput.getText().isBlank()){
                JOptionPane.showMessageDialog(this, "Name and ID cannot be empty");
            }else {
                saveChanges();
                flag = true;
            }
        });

        cancelButton.addActionListener(e -> {
            onCancel();
            flag = false;
        });

        setContentPane(mainPanel);
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);

    }

    /*
     * called upon if the save button is clicked to save the edited changes. */
    private void saveChanges(){

        name = programNameInput.getText().toUpperCase();
        pid = programIDInput.getText().toUpperCase();
        if(POSTGRADUATERadioButton.isSelected()) {
            type = "POSTGRADUATE";
        }else{
            type = "UNDERGRADUATE";
        }

        program = new Program(pid, name, type);
        dispose();

    }

    /*
     * disposes the dialog box if cancel button is press.
     * */
    private void  onCancel() {
        dispose();
    }

    /*
     * return true if there were any changes made to the original values
     * */
    public Boolean editedSuccessfully(){
        return flag;
    }

    /*
     * returns the edited program
     * */
    public Program getProgram(){
        return program;
    }

}
