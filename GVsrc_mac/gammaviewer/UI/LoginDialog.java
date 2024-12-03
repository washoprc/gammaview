/*
    GAMMA Viewer - LoginDialog : Dialog for input login infomation
    Copyright (C) 2002-2003  Tsuda Eisuke


    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA


    GAMMA Viewer - LoginDialog : �����������ѥѥͥ�
    Copyright (C) 2002-2003  ���ıѲ�


    ���Υץ����ϥե꡼���եȥ������Ǥ������ʤ��Ϥ���򡢥ե꡼����
    �ȥ��������Ĥˤ�ä�ȯ�Ԥ��줿 GNU ���̸������ѵ��������(�С�����
    ��2������˾�ˤ�äƤϤ���ʹߤΥС������Τ����ɤ줫)��������
    �β��Ǻ����ۤޤ��ϲ��Ѥ��뤳�Ȥ��Ǥ��ޤ���

    ���Υץ�����ͭ�ѤǤ��뤳�Ȥ��ä����ۤ���ޤ�����*������̵��
    ��* �Ǥ������Ȳ�ǽ�����ݾڤ��������Ū�ؤ�Ŭ�����ϡ������˼����줿
    ��Τ�ޤ�����¸�ߤ��ޤ��󡣾ܤ�����GNU ���̸������ѵ���������
    ������������

    ���ʤ��Ϥ��Υץ����ȶ��ˡ�GNU ���̸������ѵ���������ʣ��ʪ��
    ����������ä��Ϥ��Ǥ����⤷������äƤ��ʤ���С��ե꡼���եȥ���
    �����Ĥޤ����ᤷ�Ƥ�������(����� the Free Software Foundation,
    Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA)��
*/

package gammaviewer.UI;

import gammaviewer.ViewerConstants;
import gammaviewer.Data.Host;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;


public class LoginDialog extends JDialog implements ActionListener
{
  //-----------------------------------------
  /*  Fields  */
  //-----------------------------------------

  // Server setting panel
  ServerSettingPanel ServerPanel = null;

  // Text fields
  JTextField     UserNameField = new JTextField();
  JPasswordField PasswordField = new JPasswordField();

  // Panel
  JPanel ButtonPanel   = new JPanel();

  // Buttons
  JButton OkButton     = new JButton( "OK" );
  JButton CancelButton = new JButton( "Cancel" );

  // return value.
  int Answer = 0;

  // Answers
  public static final int LD_OK     = 1;
  public static final int LD_CANCEL = 2;


  //-----------------------------------------
  /*  Fields  */
  //-----------------------------------------

  public LoginDialog( JFrame autoFrame, Host autoHost, ServerSettingPanel autoPanel )
  {
    super( autoFrame, "Login failed", true );

    ServerPanel = autoPanel;

    this.getContentPane().setLayout( new GridLayout( 0, 1 ) );

    // Initialization of fields
    UserNameField.setText( autoHost.getUserName() );
    PasswordField.setText( autoHost.getPassword() );

    // Add components
    this.getContentPane().add( new JLabel( autoHost.getName() + "�ؤΥ�����˼��Ԥ��ޤ�����" ) );
    this.getContentPane().add( new JLabel( "�桼��̾�����Ϥ��Ƥ���������" ) );
    this.getContentPane().add( UserNameField );
    this.getContentPane().add( new JLabel( "�ѥ���ɤ����Ϥ��Ƥ���������" ) );
    this.getContentPane().add( PasswordField );
    this.getContentPane().add( ButtonPanel );

    // Button panel
    ButtonPanel.add( OkButton );
    ButtonPanel.add( CancelButton );

    UserNameField.addActionListener( this );
    PasswordField.addActionListener( this );
    OkButton.addActionListener( this );
    CancelButton.addActionListener( this );

    // Focus setting.
    OkButton.requestFocus();

    this.pack();

    // Set this dialog shown at center
    Dimension autoScreenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension autoDialogSize = this.getSize();
    this.setLocation( (autoScreenSize.width - autoDialogSize.width) / 2,
                      (autoScreenSize.height - autoDialogSize.height) / 2);

  }


  //-----------------------------------------
  /*  Fields  */
  //-----------------------------------------


  // Purpose   : Get methods of user name and password
  // Argument  : void
  // Return    : User name or password
  public String getUserName() { return UserNameField.getText(); }
  public String getPassword() { return String.valueOf( PasswordField.getPassword() ); }
  public int getAnswer()      { return Answer; }



  // Purpose   : Action performing
  // Argument  : ActionEvent
  // Return    : void
  public void actionPerformed( ActionEvent e )
  {
    if( e.getSource() == CancelButton )
      Answer = this.LD_CANCEL;
    else
      Answer = this.LD_OK;
    this.setVisible( false );
  }



  // Purpose   : synchronization username and password between this and server setting panel
  // Argument  : Host to show
  // Return    : void
  public void syncServerSettings( Host autoHost )
  {
    this.ServerPanel.showHostInfomation( autoHost );
  }


  // When clicked close button
  public void windowClosing( WindowEvent e )
  {
    Answer = this.LD_CANCEL;
    this.setVisible( false );
  }


}