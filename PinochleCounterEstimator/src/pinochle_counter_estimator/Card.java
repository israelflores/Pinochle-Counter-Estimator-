package pinochle_counter_estimator;

import java.util.Objects;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Card implements Comparable<Card>{
	private String name;
	private int id;
	private double xCoordinate;	
	private ImageView image;
	
	public Card(int idNumber, double xCoord){
		this(idNumber);
		this.xCoordinate = xCoord;
	}	
	
	public Card(int idNumber){
		//take each idNumbrer to its base
		this.id = (idNumber - 1) / 4 * 4 + 1;		

		switch (idNumber % 20) {
		case 1:
			name = "ace";
			break;
		case 5:
			name = "ten";
			break;
		case 9:
			name = "king";
			break;
		case 13:
			name = "queen";
			break;
		default:
			name = "jack";
		}

		name += "Of";

		switch (getSuitCode()) {
		case 1:
			name += "Spades";
			break;
		case 2:
			name += "Diamonds";
			break;
		case 3:
			name += "Clubs";
			break;
		default:
			name += "Hearts";
		}
		
		image = new ImageView(new Image("file:" + name + ".png"));
	}
			
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public ImageView getImage() {
		return image;
	}
	
	public int getSuitCode(){		
		return (this.id - 1) / 20 + 1;
	}
	
	public double getXCoord(){
		return xCoordinate;
	}
	
	public String toString(){
		return Integer.toString(this.id);
	}
		 
	@Override
	public boolean equals(Object o) {

		if (o == this) 
			return true;		

		if (!(o instanceof Card)) 
			return false;

		Card c = (Card) o;

		return this.getName().equals(c.getName());
	}
	
	@Override
    public int hashCode() {
        return Objects.hash(this.getName());
    }

	@Override
	public int compareTo(Card other) {
		return Integer.compare(this.getId(), other.getId());
	}

}



