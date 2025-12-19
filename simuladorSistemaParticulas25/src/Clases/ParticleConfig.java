// ParticleConfig.java
package Clases;

import javafx.scene.paint.Color;

public class ParticleConfig {
    
    private double velocidadX = 0;
    private double velocidadY = 0;
    private double mass = 1;
    private double carga = 0;
    private Color color = Color.BLUE;
    
    // Constructor, getters, and setters
    public ParticleConfig() {
        this.mass = 1;
        this.velocidadX = 0;
        this.velocidadY = 0;
        this.carga = 0;
        this.color = Color.BLUE;
    }
    public ParticleConfig(ParticleConfig other) {
        this.mass = other.mass;
        this.velocidadX = other.velocidadX;
        this.velocidadY = other.velocidadY;
        this.carga = other.carga;
        this.color = other.color;
    }
    public void setVelocidadX(double velocidadx){this.velocidadX = velocidadx;}
    public void setVelocidadY(double velocidady){this.velocidadY = velocidady;}
    public void setCarga(double carga){this.carga = carga;}
    public void setColor(Color color){this.color = color;}
    public void setMass(double mass){this.mass = mass;}
            
    public double getVelocidadX() {return this.velocidadX;}
    public double getVelocidadY() {return this.velocidadY;}
    public double getCarga() {return this.carga;}
    public Color getColor() {return this.color;}
    public double getMass(){return this.mass;}
    
    @Override
    public String toString() {
        return String.format("ParticleConfig[masa = %2f, carga=%.2f, vx=%.2f, vy=%.2f]", mass, carga, velocidadX, velocidadY);
    }
}