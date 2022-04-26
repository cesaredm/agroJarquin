/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.table.DefaultTableModel;
import modelo.Usuarios;
import vista.IMenu;

/**
 *
 * @author CESAR DIAZ MARADIAGA
 */
public class CtrlUsuarios implements ActionListener, CaretListener{
    IMenu menu;
    Usuarios usuarios;
    String id;
    DefaultTableModel modelo;
    public CtrlUsuarios(IMenu menu, Usuarios usuarios)
    {
        modelo = new DefaultTableModel();
        this.menu = menu;
        this.usuarios = usuarios;
        this.menu.btnGuardarUsuario.addActionListener(this);
        this.menu.btnActualizarUsuario.addActionListener(this);
        this.menu.btnNuevoUsuario.addActionListener(this);
        this.menu.mnEditarUsuarios.addActionListener(this);
        this.menu.mnBorrarUsuario.addActionListener(this);
        this.menu.txtBuscarUsuario.addCaretListener(this);
        MostrarUsuarios("");
        DeshabilitarUsuarios();
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        if(e.getSource() == menu.btnGuardarUsuario)
        {
            String nombre = menu.txtNombreUsuario.getText(), pass = menu.txtPasswordUsuario.getText(), permiso = menu.cmbPermisoUsuario.getSelectedItem().toString();
        if(nombre.equals("") || pass.equals("") || permiso.equals(""))
        {
            
        }
        else
        {
            this.usuarios.Guardar(nombre, pass, permiso);
            MostrarUsuarios("");
            LimpiarUsuarios();
            menu.btnGuardarUsuario.setEnabled(true);
            menu.btnActualizarUsuario.setEnabled(false);
        }
        }
        if(e.getSource() == menu.btnActualizarUsuario)
        {
            String nombre = menu.txtNombreUsuario.getText(), pass = menu.txtPasswordUsuario.getText(), permiso = menu.cmbPermisoUsuario.getSelectedItem().toString();
        if(nombre.equals("") || pass.equals("") || permiso.equals(""))
        {
            
        }
        else
        {
            this.usuarios.Actualizar(this.id, nombre, pass, permiso);
            MostrarUsuarios("");
            LimpiarUsuarios();
            menu.btnGuardarUsuario.setEnabled(true);
            menu.btnActualizarUsuario.setEnabled(false);
        }
        }
        if(e.getSource() == menu.btnNuevoUsuario)
        {
            HabilitarUsuarios();
            LimpiarUsuarios();
        }
        if(e.getSource() == menu.mnEditarUsuarios)
        {
            int filaseleccionada = menu.tblUsuarios.getSelectedRow();
        String nombre, pass, permiso, id;
        try {
            this.modelo = (DefaultTableModel) menu.tblUsuarios.getModel();
            if(filaseleccionada == -1)
            {
                
            }
            else
            {
                id = (String) this.modelo.getValueAt(filaseleccionada, 0); 
                nombre = (String) this.modelo.getValueAt(filaseleccionada, 1);
                pass = (String) this.modelo.getValueAt(filaseleccionada, 2);
                permiso = (String) this.modelo.getValueAt(filaseleccionada, 3);
                this.id = id;
                
                LimpiarUsuarios();
                HabilitarUsuarios();
                
                menu.txtNombreUsuario.setText(nombre);
                menu.txtPasswordUsuario.setText(pass);
                menu.cmbPermisoUsuario.setSelectedItem(permiso);
                
                menu.btnGuardarUsuario.setEnabled(false);
                menu.btnActualizarUsuario.setEnabled(true);
                
            }
        } catch (Exception err) {
            JOptionPane.showMessageDialog(null, err);
        }
     
        }
        if(e.getSource() == menu.mnBorrarUsuario)
        {
             int filaseleccionada = menu.tblUsuarios.getSelectedRow(), id;
        
        try {
            this.modelo = (DefaultTableModel) menu.tblUsuarios.getModel();
            if(filaseleccionada == -1)
            {
                
            }
            else
            {
                int confirmar = JOptionPane.showConfirmDialog(null, "Seguro Que Quieres Borra Este Usuario", "Advertencia", JOptionPane.OK_CANCEL_OPTION);
                if(confirmar == JOptionPane.YES_OPTION)
                {
                    id = Integer.parseInt(this.modelo.getValueAt(filaseleccionada, 0).toString());
                    this.usuarios.Eliminar(id);
                    MostrarUsuarios("");
                }
                
            }
        } catch (Exception err) {
            JOptionPane.showMessageDialog(null, err);
        }
        }
    }

    @Override
    public void caretUpdate(CaretEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        if(e.getSource() == menu.txtBuscarUsuario)
        {
              MostrarUsuarios(menu.txtBuscarUsuario.getText());
        }
    }
    public void MostrarUsuarios(String buscar)
    {
        menu.tblUsuarios.getTableHeader().setFont(new Font("Sugoe UI",Font.PLAIN, 14));
        menu.tblUsuarios.getTableHeader().setOpaque(false);
        menu.tblUsuarios.getTableHeader().setBackground(new Color(69,76,89));
        menu.tblUsuarios.getTableHeader().setForeground(new Color(255,255,255));
        menu.tblUsuarios.getTableHeader().setPreferredSize(new java.awt.Dimension(0, 35));
        menu.tblUsuarios.setModel(this.usuarios.Mostrar(buscar));
    }
    public void LimpiarUsuarios()
    {
        menu.txtNombreUsuario.setText("");
        menu.txtPasswordUsuario.setText("");
    }
    //metodo para Habilitar los elementos inabilitados por el metodo DeshabilitarUsuarios
    public void HabilitarUsuarios()
    {
        menu.txtNombreUsuario.setEnabled(true);
        menu.txtPasswordUsuario.setEnabled(true);
        menu.cmbPermisoUsuario.setEnabled(true);
        menu.btnGuardarUsuario.setEnabled(true);
        menu.btnActualizarUsuario.setEnabled(false);
    }
    //metodo para dehabilitar elementos de formulario Usuario
    public void DeshabilitarUsuarios()
    {
        menu.txtNombreUsuario.setEnabled(false);
        menu.txtPasswordUsuario.setEnabled(false);
        menu.cmbPermisoUsuario.setEnabled(false);
        menu.btnGuardarUsuario.setEnabled(false);
        menu.btnActualizarUsuario.setEnabled(false);
    }
}
