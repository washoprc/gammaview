/*
    GAMMA Viewer - ColorSettingPanel : Panel for color settings
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


    GAMMA Viewer - ColorSettingPanel : 色設定用パネル
    Copyright (C) 2002-2003  津田英介


    このプログラムはフリーソフトウェアです。あなたはこれを、フリーソフ
    トウェア財団によって発行された GNU 一般公衆利用許諾契約書(バージョ
    ン2か、希望によってはそれ以降のバージョンのうちどれか)の定める条件
    の下で再頒布または改変することができます。

    このプログラムは有用であることを願って頒布されますが、*全くの無保
    証* です。商業可能性の保証や特定の目的への適合性は、言外に示された
    ものも含め全く存在しません。詳しくはGNU 一般公衆利用許諾契約書をご
    覧ください。

    あなたはこのプログラムと共に、GNU 一般公衆利用許諾契約書の複製物を
    一部受け取ったはずです。もし受け取っていなければ、フリーソフトウェ
    ア財団まで請求してください(宛先は the Free Software Foundation,
    Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA)。
*/


package gammaviewer.UI;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class ColorSettingPanel extends JPanel implements ActionListener
{
  //-----------------------------------------
  /*  Fields  */
  //-----------------------------------------


  // Parent dialog
  ControlDialog ParentDialog = null;

  // Panels
  JPanel RadioButtonPanel = new JPanel();
  JPanel ColorPanel       = new JPanel();

  // Radio buttons
  JRadioButton FrameButton = new JRadioButton( "フレーム"    , true );
  JRadioButton ValueButton = new JRadioButton( "フォント"    , false );
  JRadioButton DataButton  = new JRadioButton( "グラフ"      , false );
  JRadioButton CrossButton = new JRadioButton( "クロスライン" , false );

  // Button
  JButton DefaultButton = new JButton( "デフォルト" );

  // Button group
  ButtonGroup RadioButtonGroup = new ButtonGroup();

  // Color chooser
  JColorChooser Chooser = new JColorChooser();

  // Layouts.
  GridBagLayout ColorLayout = new GridBagLayout();

  // Current checked radio button
  JRadioButton CurrentButton = FrameButton;


  //-----------------------------------------
  /*  Constructor  */
  //-----------------------------------------


  public ColorSettingPanel( ControlDialog autoParnet )
  {
    try
    {
      // Set parent dialog
      ParentDialog = autoParnet;

      // RadioButton
      RadioButtonGroup.add( FrameButton );
      RadioButtonGroup.add( ValueButton );
      RadioButtonGroup.add( DataButton );
      RadioButtonGroup.add( CrossButton );

      // Add radio buttons to panel
      RadioButtonPanel.add( FrameButton );
      RadioButtonPanel.add( ValueButton );
      RadioButtonPanel.add( DataButton );
      RadioButtonPanel.add( CrossButton );
      RadioButtonPanel.add( DefaultButton );

      // Set action command
      FrameButton.setActionCommand( "FRAME" );
      ValueButton.setActionCommand( "VALUE" );
      DataButton.setActionCommand( "DATA" );
      CrossButton.setActionCommand( "CROSS" );
      DefaultButton.setActionCommand( "DEFAULT" );

      // Add action listener
      FrameButton.addActionListener( this );
      ValueButton.addActionListener( this );
      DataButton.addActionListener( this );
      CrossButton.addActionListener( this );
      DefaultButton.addActionListener( this );

      // GridBagConstraints
      GridBagConstraints autoConstraints = new GridBagConstraints();
      autoConstraints.weightx = 1.0;
      autoConstraints.fill = GridBagConstraints.BOTH;
      autoConstraints.insets = new Insets(2, 2, 2, 2);
      autoConstraints.ipadx = autoConstraints.ipady = 2;
      autoConstraints.gridwidth = GridBagConstraints.REMAINDER;

      // Set layout
      ColorPanel.setLayout( ColorLayout );

      ColorLayout.setConstraints( RadioButtonPanel, autoConstraints );
      ColorPanel.add( RadioButtonPanel );
      ColorLayout.setConstraints( Chooser, autoConstraints );
      ColorPanel.add( Chooser );

      this.add( ColorPanel );

      /* Initialization */
      this.loadColor( FrameButton );

    }
    catch( Exception autoExcept )
    {
      autoExcept.printStackTrace();
    }
  }


  //-----------------------------------------
  /*  Methods  */
  //-----------------------------------------


  // Purpose   : Action performed
  // Argument  : Action Event
  // Return    : void
  public void actionPerformed( ActionEvent e )
  {
    // Set colors to default
    if( e.getActionCommand().equals( "DEFAULT" ) )
    {
      // Save default
      ParentDialog.getData().setFrameColor( Color.black );
      ParentDialog.getData().setValueColor( Color.black );
      ParentDialog.getData().setDataColor( Color.black );
      ParentDialog.getData().setCrossColor( Color.blue );
      // Show default
      if( CurrentButton.getActionCommand().equals( "CROSS" ) )
        Chooser.setColor( Color.blue );
      else
        Chooser.setColor( Color.black );
    }

    // Change color of properties
    else
    {
      this.setColor();
      CurrentButton = (JRadioButton)e.getSource();
      this.loadColor( CurrentButton );
    }
  }


  // Purpose   : To save current set color
  // Argument  : void
  // Return    : void
  public void setColor()
  {
    if( CurrentButton.getActionCommand().equals( "FRAME" ) )
      ParentDialog.getData().setFrameColor( Chooser.getColor() );
    else if( CurrentButton.getActionCommand().equals( "VALUE" ) )
      ParentDialog.getData().setValueColor( Chooser.getColor() );
    else if( CurrentButton.getActionCommand().equals( "DATA" ) )
      ParentDialog.getData().setDataColor( Chooser.getColor() );
    else if( CurrentButton.getActionCommand().equals( "CROSS" ) )
      ParentDialog.getData().setCrossColor( Chooser.getColor() );
  }


  // Purpose   : To load new color
  // Argument  : Button to set
  // Return    : void
  public void loadColor( JRadioButton autoButton )
  {
    if( autoButton.getActionCommand().equals( "FRAME" ) )
      Chooser.setColor( ParentDialog.getData().getFrameColor() );
    else if( autoButton.getActionCommand().equals( "VALUE" ) )
      Chooser.setColor( ParentDialog.getData().getValueColor() );
    else if( autoButton.getActionCommand().equals( "DATA" ) )
      Chooser.setColor( ParentDialog.getData().getDataColor() );
    else if( autoButton.getActionCommand().equals( "CROSS" ) )
      Chooser.setColor( ParentDialog.getData().getCrossColor() );
  }
}