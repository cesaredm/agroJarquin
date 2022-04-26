/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import controlador.CtrlFacturacion;
import controlador.CtrlPagos;

/**
 *
 * @author CESAR DIAZ MARADIAGA
 */
public class SocketServer implements Runnable{
	ServerSocket server;
	Socket cliente;
	private int port = 6000;
	ObjectInputStream input;
	Facturacion factura;
	PagosCreditos pagos;
	CtrlFacturacion facturacionController;
	CtrlPagos pagosContoller;
	static String objeto;
	Thread hiloServidor;
	public SocketServer(){
		this.factura = new Facturacion();
		this.pagos = new PagosCreditos();
		this.hiloServidor = new Thread(this);
		this.hiloServidor.start();
	}	

	public void setPagosController(CtrlPagos pagosController){
		this.pagosContoller = pagosController;
	}

	public void setfacturacionController(CtrlFacturacion facturacionController){
		this.facturacionController = facturacionController;
	}
	
	//establecer el puerto de escucha de peticiones del sevidor	
	public void listenPort(){
		try {
			this.server = new ServerSocket(this.port);
		} catch (IOException ex) {
			Logger.getLogger(SocketServer.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	//poner en escucha de peticiones al servidor
	public void listenServer(){
		try {
			this.cliente = this.server.accept();
		} catch (IOException ex) {
			Logger.getLogger(SocketServer.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	//crear el flujo de entrada
	public void createInputFlow(){
		try {
			this.input = new ObjectInputStream(this.cliente.getInputStream());
		} catch (IOException ex) {
			Logger.getLogger(SocketServer.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	//recibir la informacion del flujo de entrada
	public void getInput(){
		try {
			String dato = ((Object) this.input.readObject().getClass().getSimpleName()).toString();
			switch(dato){
				case "Facturacion": {
					this.factura = (Facturacion) this.input.readObject();
					this.facturacionController.updateNumberFactura(this.factura.ObtenerIdFactura());
				}break;
				case "PagosCreditos": {
					this.pagos = (PagosCreditos) this.input.readObject();
					
					this.pagosContoller.UltimoPago();
				}break;
			}
		} catch (IOException ex) {
			Logger.getLogger(SocketServer.class.getName()).log(Level.SEVERE, null, ex);
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(SocketServer.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	//cerrar el flujo de entrada
	public void closeInputFlow(){
		try {
			this.input.close();
		} catch (IOException ex) {
			Logger.getLogger(SocketServer.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Override
	public void run() {
		while (true) {			
			this.listenServer();
			this.createInputFlow();
			this.getInput();
			this.closeInputFlow();
		}
	}
}
