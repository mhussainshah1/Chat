/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package help;

import java.awt.Insets;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

/**
 *
 * @author m_hus
 */
public class TestClientGUI extends javax.swing.JFrame {

    private DefaultListModel<String> listModel;

    /**
     * Creates new form TestClientGUI
     */
    public TestClientGUI() {

        initComponents();

        listModel = new DefaultListModel<>();
        listModel.addElement("Jane Doe");
        listModel.addElement("John Smith");
        listModel.addElement("Kathy Green");

        userCanvas.setModel(listModel);

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jtextFilDiscuSP = new javax.swing.JScrollPane();
        jtextFilDiscu = new javax.swing.JTextPane();
        jsplitUserPane = new javax.swing.JScrollPane();
        jtextListUsers = new javax.swing.JTextPane();
        jtextInputChatSP = new javax.swing.JScrollPane();
        jtextInputChat = new javax.swing.JTextField();
        jsbtn = new javax.swing.JButton();
        jsbtndeco = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        userCanvas = new javax.swing.JList<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Chat");
        setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        setSize(new java.awt.Dimension(700, 500));
        getContentPane().setLayout(null);

        jtextFilDiscu.setEditable(false);
        jtextFilDiscu.setContentType("text/html"); // NOI18N
        jtextFilDiscu.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        jtextFilDiscu.setMargin(new Insets(6,6,6,6));
        jtextFilDiscuSP.setViewportView(jtextFilDiscu);

        getContentPane().add(jtextFilDiscuSP);
        jtextFilDiscuSP.setBounds(10, 0, 290, 200);

        jtextListUsers.setEditable(false);
        jtextListUsers.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        jtextListUsers.setMargin(new Insets(6,6,6,6));
        jsplitUserPane.setViewportView(jtextListUsers);

        getContentPane().add(jsplitUserPane);
        jsplitUserPane.setBounds(310, 0, 80, 200);

        jtextInputChat.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        jtextInputChat.setToolTipText("");
        jtextInputChat.setMargin(new Insets(6,6,6,6));
        jtextInputChat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtextInputChatKeyPressed(evt);
            }
        });
        jtextInputChatSP.setViewportView(jtextInputChat);

        getContentPane().add(jtextInputChatSP);
        jtextInputChatSP.setBounds(10, 210, 380, 30);

        jsbtn.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        jsbtn.setText("Send");
        jsbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jsbtnActionPerformed(evt);
            }
        });
        getContentPane().add(jsbtn);
        jsbtn.setBounds(10, 250, 80, 27);

        jsbtndeco.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        jsbtndeco.setText("Disconnect");
        jsbtndeco.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jsbtndecoActionPerformed(evt);
            }
        });
        getContentPane().add(jsbtndeco);
        jsbtndeco.setBounds(280, 250, 110, 27);

        userCanvas.setName(""); // NOI18N
        userCanvas.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                userCanvasValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(userCanvas);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(400, 0, 100, 200);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jtextInputChatKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtextInputChatKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtextInputChatKeyPressed

    private void jsbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jsbtnActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_jsbtnActionPerformed

    private void userCanvasValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_userCanvasValueChanged
        // TODO add your handling code here:
        JOptionPane.showMessageDialog(this, "Item Clicked");
    }//GEN-LAST:event_userCanvasValueChanged

    private void jsbtndecoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jsbtndecoActionPerformed
        // TODO add your handling code here:
        listModel.removeElement(jtextInputChat.getText());
        //listModel.clear();
    }//GEN-LAST:event_jsbtndecoActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TestClientGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TestClientGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TestClientGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TestClientGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new TestClientGUI().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton jsbtn;
    private javax.swing.JButton jsbtndeco;
    private javax.swing.JScrollPane jsplitUserPane;
    private javax.swing.JTextPane jtextFilDiscu;
    private javax.swing.JScrollPane jtextFilDiscuSP;
    private javax.swing.JTextField jtextInputChat;
    private javax.swing.JScrollPane jtextInputChatSP;
    private javax.swing.JTextPane jtextListUsers;
    private javax.swing.JList<String> userCanvas;
    // End of variables declaration//GEN-END:variables
}
