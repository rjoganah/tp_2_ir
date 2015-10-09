/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package informationretrieval;

/**
 *
 * @author robinjoganah
 */
public class QueryIR {
    
    private String texte;
    private String numero;
    
    QueryIR()
    {
        this.numero = "";
        this.texte = "";
    }
    QueryIR(String texte, String numero)
    {
        this.numero = numero;
        this.texte = texte;
    }
    public String getTexte()
    {
        return this.texte;
    }
    public String getNumero()
    {
        return this.numero;
    }
    public void setTexte(String texte)
    {
        this.texte = texte;
    }
    public void setNumero(String numero)
    {
        this.numero = numero;
    }
    
    
}
