/*
    GAMMA Viewer - PortSettingPanel : Panel for port settings
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


    GAMMA Viewer - PortSettingPanel : �ݡ��������ѥѥͥ�
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

import gammaviewer.InfomationException;
import gammaviewer.ViewerConstants;
import gammaviewer.Data.FilterLoader;
import gammaviewer.Data.PortInfomation;
import gammaviewer.Data.ViewerDatas;
import gammaviewer.Data.AbstractFilter;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.io.*;
import java.util.*;


public class PortSettingPanel extends JPanel implements ListSelectionListener,
                                                        ActionListener
{
  //-----------------------------------------
  /*  Fields  */
  //-----------------------------------------


  // Parent dialog
  ControlDialog ParentDialog = null;

  // Panels.
  JPanel ModifyListPanel = new JPanel();
  JPanel ListPanel       = new JPanel();
  JPanel PortPanel       = new JPanel();
  JPanel PortInfoPanel   = new JPanel();
  JPanel PortButtonPanel = new JPanel();

  // Layouts.
  GridBagLayout RootLayout     = new GridBagLayout();
  GridLayout ModifyListLayout  = new GridLayout( 1, 3 );
  GridLayout PortInfoLayout    = new GridLayout( 0, 2 );
  GridBagLayout PortLayout     = new GridBagLayout();

  // List
  JList PortList = new JList();
  // Scroll pane
  JScrollPane ListScrollPane = new JScrollPane();

  // Combo box
  JComboBox FilterBox = new JComboBox();

  // Labels
  JLabel NameLabel         = new JLabel("�ݡ���̾��");
  JLabel RelatedLabel      = new JLabel("��Ϣ����ݡ���");
  JLabel SamplingLabel     = new JLabel("����ץ�󥰥�����(��s)");
  JLabel TriggerLabel      = new JLabel("�ȥꥬ��������(ms)");
  JLabel FilterLabel       = new JLabel("�ե��륿���μ���");
  JLabel MaxValLabel       = new JLabel("�ݡ��Ⱥ�����");
  JLabel MinValLabel       = new JLabel("�ݡ��ȺǾ���");
  JLabel MaxDrawValLabel   = new JLabel("ɽ��������");
  JLabel MinDrawValLabel   = new JLabel("ɽ���Ǿ���");
  JLabel StartTimeLabel    = new JLabel("�ץ�åȳ��ϻ���(ms)");
  JLabel EndTimeLabel      = new JLabel("�ץ�åȽ�λ����(ms)");

  // Text field
  JTextField NameField         = new JTextField( 20 );
  JTextField RelatedField      = new JTextField( 300 );
  JTextField SamplingTimeField = new JTextField( 10 );
  JTextField TriggerTimeField  = new JTextField( 10 );
  JTextField MaxValField       = new JTextField( 15 );
  JTextField MinValField       = new JTextField( 15 );
  JTextField MaxDrawValField   = new JTextField( 15 );
  JTextField MinDrawValField   = new JTextField( 15 );
  JTextField StartTimeField    = new JTextField( 15 );
  JTextField EndTimeField      = new JTextField( 15 );

  // Buttons.
  JButton AddButton     = new JButton( "�ɲ�" );
  JButton ChengeButton  = new JButton( "�ѹ�" );
  JButton DelButton     = new JButton( "���" );


  //-----------------------------------------
  /*  Constructor  */
  //-----------------------------------------

  public PortSettingPanel( ControlDialog autoParnet )
  {
    // Set parent dialog
    ParentDialog = autoParnet;

    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }


  //-----------------------------------------
  /*  Methods  */
  //-----------------------------------------

  private void jbInit() throws Exception
  {
    // Dialog main layout
    this.setLayout( RootLayout );

    // GridBagConstraints
    GridBagConstraints autoConstraints = new GridBagConstraints();
    autoConstraints.weightx      = 1.0;
    autoConstraints.fill         = GridBagConstraints.HORIZONTAL;
    autoConstraints.insets       = new Insets(2, 2, 2, 2);
    autoConstraints.ipadx        = autoConstraints.ipady = 2;
    autoConstraints.gridwidth    = GridBagConstraints.REMAINDER;

    // Port panel
    PortPanel.setLayout( PortLayout );
    PortLayout.setConstraints( PortInfoPanel, autoConstraints );
    PortPanel.add( PortInfoPanel );

    // Port info panel
    PortInfoPanel.setLayout( PortInfoLayout );
    PortInfoPanel.add( NameLabel );
    PortInfoPanel.add( NameField );
    PortInfoPanel.add( RelatedLabel );
    PortInfoPanel.add( RelatedField );
    PortInfoPanel.add( SamplingLabel );
    PortInfoPanel.add( SamplingTimeField );
    PortInfoPanel.add( TriggerLabel );
    PortInfoPanel.add( TriggerTimeField );
    PortInfoPanel.add( FilterLabel );
    PortInfoPanel.add( FilterBox );
    PortInfoPanel.add( MaxValLabel );
    PortInfoPanel.add( MaxValField );
    PortInfoPanel.add( MinValLabel );
    PortInfoPanel.add( MinValField );
    PortInfoPanel.add( MaxDrawValLabel );
    PortInfoPanel.add( MaxDrawValField );
    PortInfoPanel.add( MinDrawValLabel );
    PortInfoPanel.add( MinDrawValField );
    PortInfoPanel.add( StartTimeLabel );
    PortInfoPanel.add( StartTimeField );
    PortInfoPanel.add( EndTimeLabel );
    PortInfoPanel.add( EndTimeField );

    // Port button panel
    PortButtonPanel.add( AddButton );
    PortButtonPanel.add( ChengeButton );
    PortButtonPanel.add( DelButton );
    AddButton.addActionListener( this );
    DelButton.addActionListener( this );
    ChengeButton.addActionListener( this );

    // List Panel
    PortList.addListSelectionListener( this );
    PortList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
    ListScrollPane.getViewport().setView( PortList );
    ListPanel.add( ListScrollPane );


    // Add components to rootpanel
    autoConstraints.gridwidth = GridBagConstraints.RELATIVE;
    autoConstraints.fill = GridBagConstraints.BOTH;
    RootLayout.setConstraints( ListPanel, autoConstraints );
    this.add( ListPanel );

    autoConstraints.gridwidth = GridBagConstraints.REMAINDER;
    autoConstraints.fill = GridBagConstraints.HORIZONTAL;
    RootLayout.setConstraints( PortPanel, autoConstraints );
    this.add( PortPanel );

    RootLayout.setConstraints( PortButtonPanel, autoConstraints );
    this.add( PortButtonPanel );


    /* Initialize all components */

    AddButton.setActionCommand( "ADDPORT" );
    ChengeButton.setActionCommand( "CHANGEPORT" );
    DelButton.setActionCommand( "DELPORT" );

    ViewerDatas data = ParentDialog.getData();
    PortList.setListData( data.getRegisteredPorts() );

    // Port list initialization
    PortList.setVisibleRowCount( 13 );
    ListScrollPane.getViewport().setViewSize( PortList.getPreferredScrollableViewportSize() );
    this.setFilterList();

    // Set first element is selected
    PortList.setSelectedIndex( 0 );
    PortInfomation autoPortInfo = ParentDialog.getData().getPortInfo( PortList.getSelectedValue() );

    this.restoreValue( autoPortInfo );
  }


  // Purpose   : To syncronize selected values of portlist and port names
  // Argument  : List Selected Event
  // Return    : void
  public void valueChanged( ListSelectionEvent e )
  {
    try
    {
      // If zero size list
      if( PortList.getModel().getSize() < 1 )
        return;

      // get a selected item
      Object autoSelected = PortList.getSelectedValue();

      // If selected off
      if( autoSelected == null )
        autoSelected = PortList.getModel().getElementAt(0);

      // Get port infomation
      PortInfomation autoPortInfo = ParentDialog.getData().getPortInfo( autoSelected );
      this.restoreValue( autoPortInfo );
    }
    catch( InfomationException autoInfoExcpt )
    {
      JOptionPane.showMessageDialog( null, autoInfoExcpt.toString(), "Error", JOptionPane.ERROR_MESSAGE );
    }
  }


  // Purpose   : To set filter list
  // Argument  : void
  // Return    : void
  public void setFilterList()
  {
    Vector autoList = ParentDialog.getData().getFilterLoader().getFilterNameList();
    FilterBox.removeAllItems();
    for( int i = 0; i < autoList.size(); i++ )
      FilterBox.addItem( autoList.elementAt(i) );
  }


  // Purpose   : Set port infomation to text feilds
  // Argument  : PortInfomation of chesen port
  // Return    : void
  private void restoreValue( PortInfomation autoPortInfo )
  {
    NameField.setText( autoPortInfo.getPortName() );
    RelatedField.setText( autoPortInfo.getRelatedPorts() );
    SamplingTimeField.setText( String.valueOf( autoPortInfo.getSamplingTime() ) );
    TriggerTimeField.setText( String.valueOf( autoPortInfo.getTriggerTime() ) );
    MaxValField.setText( String.valueOf( autoPortInfo.getMaxValue() ) );
    MinValField.setText( String.valueOf( autoPortInfo.getMinValue() ) );
    MaxDrawValField.setText( String.valueOf( autoPortInfo.getMaxDrawValue() ) );
    MinDrawValField.setText( String.valueOf( autoPortInfo.getMinDrawValue() ) );
    StartTimeField.setText( String.valueOf( autoPortInfo.getStartTime() ) );
    EndTimeField.setText( String.valueOf( autoPortInfo.getEndTime() ) );
    FilterBox.setSelectedItem( autoPortInfo.getFilterName() );
  }


  // Purpose   : Set port infomation to text feilds
  // Argument  : PortInfomation of chesen port
  // Return    : void
  private PortInfomation createSetPort() throws InfomationException
  {
    // Get filter
    FilterLoader autoLoader = ParentDialog.getData().getFilterLoader();
    AbstractFilter autoFilter = autoLoader.getFilter( FilterBox.getSelectedItem().toString() );

    // Create new port
    PortInfomation autoNewPort = new PortInfomation();

    autoNewPort.setPortName    ( NameField.getText() );
    autoNewPort.setRelatedPorts( RelatedField.getText() );
    autoNewPort.setSamplingTime( Double.valueOf( SamplingTimeField.getText() ).doubleValue() );
    autoNewPort.setTriggerTime ( Double.valueOf( TriggerTimeField.getText() ).doubleValue() );
    autoNewPort.setRange       ( Double.valueOf( MaxValField.getText() ).doubleValue(),
                                 Double.valueOf( MinValField.getText() ).doubleValue() );
    autoNewPort.setDrawRange   ( Double.valueOf( MaxDrawValField.getText() ).doubleValue(),
                                 Double.valueOf( MinDrawValField.getText() ).doubleValue() );
    autoNewPort.setTimeRange   ( Double.valueOf( StartTimeField.getText() ).doubleValue(),
                                 Double.valueOf( EndTimeField.getText() ).doubleValue() );
    autoNewPort.setFilter      ( autoFilter );

    return autoNewPort;
  }


  // Purpose   : Action performed
  // Argument  : Action Event
  // Return    : void
  public void actionPerformed( ActionEvent e )
  {
    try
    {
      if( e.getActionCommand().equals( "ADDPORT" ) )
      {
        PortInfomation autoNewPort = this.createSetPort();
        if( autoNewPort == null )
          return;
        ParentDialog.getData().addPort( autoNewPort );

        // Set new port list
        PortList.setListData( ParentDialog.getData().getRegisteredPorts() );
        PortList.setSelectedValue( autoNewPort.getPortName(), true );
        this.restoreValue( autoNewPort );
        ParentDialog.updateSettings();
      }

      // Delete port
      else if( e.getActionCommand().equals( "DELPORT" ) )
      {
        int autoAnswer =
          JOptionPane.showConfirmDialog( null, "�ݡ��Ȥ������ޤ���������Ǥ�����", "���",
                                          JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE );

        if( autoAnswer == JOptionPane.YES_OPTION )
        {
          if( PortList.getModel().getSize() < 1 )
            throw new InfomationException( "�ݡ��Ȥ����ƺ������ޤ��󡣺��㣱�Ĥ�ɬ�פǤ���" );

          // Delete it
          Object autoSelected = PortList.getSelectedValue();
          if( autoSelected != null )
            ParentDialog.getData().delPort( autoSelected );

          // Set new port list
          Vector autoList = ParentDialog.getData().getRegisteredPorts();
          PortList.setListData( autoList );
          PortList.setSelectedIndex( 0 );

          ParentDialog.updateSettings();
        }
      }
      else if( e.getActionCommand().equals( "CHANGEPORT" ) )
      {
        int autoAnswer =
          JOptionPane.showConfirmDialog( null, "���Ƥ��ѹ����ޤ���������Ǥ�����", "�ѹ�",
                                          JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE );

        if( autoAnswer == JOptionPane.YES_OPTION )
        {
          PortInfomation autoNewPort = this.createSetPort();
          ParentDialog.getData().replacePort( autoNewPort, PortList.getSelectedValue() );
          PortList.setListData( ParentDialog.getData().getRegisteredPorts() );
          PortList.setSelectedValue( autoNewPort.getPortName(), true );
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
                               "PortSetting line:213, " + autoExcpt.toString() + ":" + e.getActionCommand() };
      JOptionPane.showMessageDialog( null, autoMessage, "Ϣ��򤪴ꤤ���ޤ���", JOptionPane.ERROR_MESSAGE );
    }
  }
}