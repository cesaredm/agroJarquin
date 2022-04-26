/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import vista.IMenu;
import modelo.Utilidades;

/**
 *
 * @author CESAR DIAZ MARADIAGA
 */
public class CtrlUtilidades implements ActionListener{
    IMenu menu = new IMenu();
    Utilidades utilidades = new Utilidades();
    
 public CtrlUtilidades(IMenu menu , Utilidades utilidades){
     this.menu = menu;
     this.utilidades = utilidades;
     this.menu.btnMostrarUtilidades.addActionListener(this);
 }   

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == menu.btnMostrarUtilidades){
            
        }
    }
}
