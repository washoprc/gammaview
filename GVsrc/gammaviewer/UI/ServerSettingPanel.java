/*
    GAMMA Viewer - ServerSettingPanel : Panel for setting servers
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


    GAMMA Viewer - ServerSettingPanel : �����������ѥѥͥ�
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
import gammaviewer.InfomationException;
import gammaviewer.Data.ViewerDatas;
import gammaviewer.Data.Host;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.Vector;


public class ServerSettingPanel extends JPanel implements ActionListener
{
  //-----------------------------------------
  /*  Fields  */
  //-----------------------------------------


  // Parent dialog
  ControlDialog ParentDialog = null;

  // Panels.
  JPanel ServerTitlePanel      = new JPanel();
  JPanel ServerInfoPanel       = new JPanel();
  JPanel ServerSavePasswdPanel = new JPanel();
  JPanel ServerButtonPanel     = new JPanel();

  // Lables
  JLabel ServerTitleLabel     = new JLabel( "�����Ф�����" );
  JLabel ServerLabel          = new JLabel( "������̾" );
  JLabel AccountLabel         = new JLabel( "�桼��̾" );
  JLabel PasswordLabel        = new JLabel( "�ѥ����" );
  JLabel PortLabel            = new JLabel( "��³�ݡ���" );
  JLabel ShotPassLabel        = new JLabel( "����åȥѥ�" );
  JLabel ServerTypeLabel      = new JLabel( "�����Фμ���" );

  // Server list
  JComboBox ServerBox     = new JComboBox();
  JComboBox ServerTypeBox = new JComboBox();

  // Text fields.
  JTextField HostNameField       = new JTextField( 30 );
  JTextField UserNameField       = new JTextField( 30 );
  JPasswordField PasswordField   = new JPasswordField( 40 );
  JTextField PortField           = new JTextField( 5 );
  JTextField ShotPassField       = new JTextField( 50 );

  // Buttons.
  JButton AddButton        = new JButton( "�ɲ�" );
  JButton ChangeButton     = new JButton( "�ѹ�" );
  JButton DelButton        = new JButton( "���" );

  // Layouts.
  GridBagLayout ServerLayout   = new GridBagLayout();
  GridLayout ServerTitleLayout = new GridLayout(0,1);
  GridLayout ServerInfoLayout  = new GridLayout(0,2);


  //-----------------------------------------
  /*  Constructor  */
  //-----------------------------------------

  public ServerSettingPanel( ControlDialog autoParnet )
  {
    // Set parent dialog
    ParentDialog = autoParnet;

    try
    {
      jbInit();
    }
    catch( Exception autoExcept )
    {
      autoExcept.printStackTrace();
    }
  }


  //-----------------------------------------
  /*  Methods  */
  //-----------------------------------------

  /**����ݡ��ͥ�Ȥν����*/
  private void jbInit() throws Exception
  {
    // Components layouting

    // GridBagConstraints
    GridBagConstraints autoConstraints = new GridBagConstraints();
    autoConstraints.weightx       = 1.0;
    autoConstraints.fill          = GridBagConstraints.BOTH;
    autoConstraints.insets        = new Insets(2, 2, 2, 2);
    autoConstraints.ipadx         = autoConstraints.ipady = 2;
    autoConstraints.gridwidth     = GridBagConstraints.REMAINDER;

    // Root panel
    this.setLayout( ServerLayout );
    ServerLayout.setConstraints( ServerTitlePanel, autoConstraints );
    this.add( ServerTitlePanel );
    ServerLayout.setConstraints( ServerInfoPanel, autoConstraints );
    this.add( ServerInfoPanel );
    ServerLayout.setConstraints( ServerButtonPanel, autoConstraints );
    this.add( ServerButtonPanel );

    // Sub server panels
    ServerTitlePanel.setLayout( ServerTitleLayout );
    ServerTitlePanel.add( ServerTitleLabel );
    ServerTitlePanel.add( ServerBox );
    ServerInfoPanel.setLayout( ServerInfoLayout );
    ServerInfoPanel.add( ServerLabel );
    ServerInfoPanel.add( HostNameField );
    ServerInfoPanel.add( AccountLabel );
    ServerInfoPanel.add( UserNameField );
    ServerInfoPanel.add( PasswordLabel );
    ServerInfoPanel.add( PasswordField );
    ServerInfoPanel.add( PortLabel );
    ServerInfoPanel.add( PortField );
    ServerInfoPanel.add( ShotPassLabel );
    ServerInfoPanel.add( ShotPassField );
    ServerInfoPanel.add( ServerTypeLabel );
    ServerInfoPanel.add( ServerTypeBox );
    ServerButtonPanel.add( AddButton );
    ServerButtonPanel.add( ChangeButton );
    ServerButtonPanel.add( DelButton );

    /* Initialize components */

    // Set action commands
    ServerBox.setActionCommand( "CHANGE_HOST" );
    AddButton.setActionCommand( "ADD" );
    DelButton.setActionCommand( "DEL" );
    ChangeButton.setActionCommand( "CHANGE" );
    // Set action listener
    ServerBox.addActionListener( this );
    AddButton.addActionListener( this );
    DelButton.addActionListener( this );
    ChangeButton.addActionListener( this );

    // Set data to components
    ViewerDatas autoData = ParentDialog.getData();
    Vector autoServerList = autoData.getHostList();

    // Set server list
    for( int i = 0; i < autoServerList.size(); i++ )
      ServerBox.addItem( autoServerList.elementAt(i).toString() );

    // Add server types
    ServerTypeBox.addItem( "FTP" );
    ServerTypeBox.addItem( "Windows��ͭ" );
    ServerTypeBox.addItem( "AppleTalk" );
    ServerTypeBox.addItem( "LocalFile" );

    // Set server info
    if( autoServerList.size() > 0 )
    {
      Host autoHost = autoData.getHost( 0 );
      ServerBox.setSelectedItem( autoHost.getName() );
      this.showHostInfomation( autoHost );
    }
  }


  // Purpose   : Set or get server type
  // Argument  : void or integer meaning type
  // Return    : integer meaning type or void
  private int detectServerType()
  {
    String autoSelected = (String)ServerTypeBox.getSelectedItem();
    if( autoSelected.equals( "FTP" ) )
      return ViewerConstants.TYPE_FTP;
    else if( autoSelected.equals( "Windows��ͭ" ) )
      return ViewerConstants.TYPE_SMB;
    else if( autoSelected.equals( "AppleTalk" ) )
      return ViewerConstants.TYPE_MAC;
    else if( autoSelected.equals( "LocalFile" ) )
      return ViewerConstants.TYPE_LOCAL;
    else
      return ViewerConstants.TYPE_FTP;
  }
  private void setServerType( int autoType )
  {
    if( autoType == ViewerConstants.TYPE_FTP )
      ServerTypeBox.setSelectedItem( "FTP" );
    else if( autoType == ViewerConstants.TYPE_SMB )
      ServerTypeBox.setSelectedItem("Windows��ͭ" );
    else if( autoType == ViewerConstants.TYPE_MAC )
      ServerTypeBox.setSelectedItem("AppleTalk" );
    else if( autoType == ViewerConstants.TYPE_LOCAL )
      ServerTypeBox.setSelectedItem("LocalFile" );
    else
      ServerTypeBox.setSelectedItem( "FTP" );
  }


  // Purpose   : Save server infomation
  // Argument  : Class host to show data
  // Return    : void
  synchronized public void showHostInfomation( Host autoHost )
  {
    HostNameField.setText( autoHost.getName() );
    PortField.setText( String.valueOf( autoHost.getConnectionPort() ) );
    UserNameField.setText( autoHost.getUserName() );
    PasswordField.setText( autoHost.getPassword() );
    ShotPassField.setText( autoHost.getShotPath() );
    this.setServerType( autoHost.getServerType() );
  }


  // Purpose   : Create host object to use shown host data
  // Argument  : void
  // Return    : New host object that shown
  public Host getShownHost() throws InfomationException, NumberFormatException
  {
    // Create new host object
    Host autoNewHost = new Host();

    autoNewHost.setHost          ( HostNameField.getText() );
    autoNewHost.setConnectionPort( Integer.parseInt( PortField.getText() ) );
    autoNewHost.setUserName      ( UserNameField.getText() );
    autoNewHost.setPassword      ( String.valueOf( PasswordField.getPassword() ) );
    autoNewHost.setShotPath      ( ShotPassField.getText() );
    autoNewHost.setServerType    ( this.detectServerType() );

    // Return this.
    return autoNewHost;
  }


  // Purpose   : Action performing
  // Argument  : ActionEvent
  // Return    : void
  public void actionPerformed( ActionEvent e )
  {
    try
    {
      ViewerDatas autoData = ParentDialog.getData();

      // Change host data
      if( e.getActionCommand() == "CHANGE_HOST" )
      {
        // Set server info
        String autoSelected = ServerBox.getSelectedItem().toString();
        Host autoHost = (Host)ParentDialog.getData().getHost( autoSelected );
        this.showHostInfomation( autoHost );
      }

      // Add, delete, and change host
      else if( e.getActionCommand().equals( "ADD" ) )
      {
        // Add shown host into ViewerData class
        Host autoNewHost = getShownHost();

        autoData.addServer( autoNewHost );
        ServerBox.addItem( autoNewHost.getName() );
        ServerBox.setSelectedItem( autoNewHost.getName() );
        this.showHostInfomation( autoNewHost );

        ParentDialog.updateSettings();
      }
      else if( e.getActionCommand().equals( "DEL" ) )
      {
        int autoAnswer =
          JOptionPane.showConfirmDialog( null, "���Υ����Ф������ޤ���������Ǥ�����", "���",
                                          JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE );

        if( autoAnswer == JOptionPane.YES_OPTION )
        {
          // If this is last one
          if( ServerBox.getModel().getSize() < 1 )
            throw new InfomationException( "�����Ф����ƺ������ޤ��󡣺��㣱�Ĥ�ɬ�פǤ���" );

          String autoName = HostNameField.getText();

          // delete an item
          autoData.delServer( autoName );
          ServerBox.removeItem( autoName );

          // Set default item
          ServerBox.setSelectedIndex( 0 );
          Host autoHost = ParentDialog.getData().getHost( 0 );
          this.showHostInfomation( autoHost );
          ParentDialog.updateSettings();
        }
      }
      else if( e.getActionCommand().equals( "CHANGE" ) )
      {
        int autoAnswer =
          JOptionPane.showConfirmDialog( null, "������ѹ����ޤ���������Ǥ�����", "�ѹ�",
                                          JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE );

        if( autoAnswer == JOptionPane.YES_OPTION )
        {
          Object autoSelected = ServerBox.getSelectedItem();
          Host autoHost = getShownHost();
          autoData.replaceServer( autoHost, autoSelected );
          ServerBox.removeItem( autoSelected );
          ServerBox.addItem( autoHost.getName() );
          ServerBox.setSelectedItem( autoHost.getName() );
          ParentDialog.updateSettings();
        }
      }
    }
    catch( InfomationException autoInfoExcpt )
    {
      JOptionPane.showMessageDialog( null, autoInfoExcpt.toString(), "Error", JOptionPane.ERROR_MESSAGE );
    }
    catch( NumberFormatException autoNmbrExcpt )
    {
      Object[] autoMessage = { "�����Ǥ���٤��Ȥ��������Ǥ���ޤ���",
                               "����ץ�󥰥����ࡢ����Ǿ������򸫤ʤ����ƤߤƤ���������" };
      JOptionPane.showMessageDialog( null, autoMessage, "Error", JOptionPane.ERROR_MESSAGE );
    }
    catch( Exception autoExcpt )
    {
      Object[] autoMessage = { "ͽ�����ʤ��㳰��ȯ�����ޤ�����",
                               "�ʲ��ξ������ƥʡ�����𤷤Ƥ���������Ƚ�����ޤ���",
                               "DetailSetting line:213, " + autoExcpt.toString() + ":" + e.getActionCommand() };
      JOptionPane.showMessageDialog( null, autoMessage, "Ϣ��򤪴ꤤ���ޤ���", JOptionPane.ERROR_MESSAGE );
    }
  }
}