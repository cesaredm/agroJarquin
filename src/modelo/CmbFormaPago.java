/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;
import java.sql.*;

/**
 *
 * @author CESAR DIAZ MARADIAGA
 */
public class CmbFormaPago extends Conexiondb{
	private int id;
	private String nombre;

	public CmbFormaPago(int id, String nombre){
		this.id = id;
		this.nombre = nombre;
	}

	public int getId() {
		return id;
	}

	public String getNombre() {
		return nombre;
	}

	@Override
	public boolean equals(Object obj){
		if(obj == null)	
		{
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;	
		}
		final CmbFormaPago other = (CmbFormaPago) obj;
		if (other.id != other.getId()) {
			return false;	
		}
		return true;
	}

	@Override
	public String toString(){
		return this.nombre;	
	}
}
