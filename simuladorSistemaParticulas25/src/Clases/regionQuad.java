package Clases;

import javafx.geometry.Point2D;

public class regionQuad {
    
    public double x,y; //centro del cuadrante
    public double width;//ancho
    public double height;//alto
    
    public regionQuad(double x, double y, double width, double height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }   
    //dividir en 4 regiones mas como cuadrantes de coordenadas de centro x e y
    public regionQuad[] dividirRegion(){
        double quarterWidth = width/4;  // Un cuarto del ancho
    double quarterHeight = height/4; // Un cuarto del alto
    
    return new regionQuad[] {
        // NE - arriba derecha
        new regionQuad(x + quarterWidth, y - quarterHeight, width/2, height/2),
        // NW - arriba izquierda  
        new regionQuad(x - quarterWidth, y - quarterHeight, width/2, height/2),
        // SW - abajo izquierda
        new regionQuad(x - quarterWidth, y + quarterHeight, width/2, height/2),
        // SE - abajo derecha
        new regionQuad(x + quarterWidth, y + quarterHeight, width/2, height/2)
        };
    }
    //funcion que comprueba si un punto esta dentro de la region
    public boolean contiene(Point2D punto){
        return ((punto.getX() <= x + width/2) && (punto.getX() >= x -width/2) )&& //comprobar que x este dentro de la region
               ((punto.getY() <= y + height/2) && (punto.getY() >= y - height/2));//comprobar que y este dentro de la region
    }
}
