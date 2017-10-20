package org.apache.commons.math.optimization.linear;
























public enum Relationship
{
  EQ("="), 
  

  LEQ("<="), 
  

  GEQ(">=");
  


  private final String stringValue;
  

  private Relationship(String stringValue)
  {
    this.stringValue = stringValue;
  }
  

  public String toString()
  {
    return stringValue;
  }
  



  public Relationship oppositeRelationship()
  {
    switch (1.$SwitchMap$org$apache$commons$math$optimization$linear$Relationship[ordinal()]) {
    case 1: 
      return GEQ;
    case 2: 
      return LEQ;
    }
    return EQ;
  }
}
