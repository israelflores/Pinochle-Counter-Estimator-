package pinochle_counter_estimator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class Hand {

	private ArrayList<Card> cards;
	public int[] idArray;
	
	// cardSet 
	public Hand(){
		cards = new ArrayList<>();			
	}
	
	// myHand (new random hand)
	public Hand (double xCoordOfLeftMostCard, double cardSpacing){
		cards = new ArrayList<>();		
		idArray = new int[30];
		Hand deck = new Hand();
		deck.createFullDeck();
		deck.shuffle();
		
		for(int i = 0; i < 30; i++)	
			idArray[i] = deck.getCard(i).getId();		
		
		Arrays.sort(idArray);

		for (int i = 0; i < 30; i++)			
			addCard(idArray[i], xCoordOfLeftMostCard + i*cardSpacing);
				
	}
	
	// myHand (play previous hand)
	public Hand (int[] previousIdArray, double xCoordOfLeftMostCard, double cardSpacing){
		cards = new ArrayList<>();
		idArray = previousIdArray.clone();

		for(int i = 0; i < previousIdArray.length; i++){			
			addCard(new Card(previousIdArray[i], xCoordOfLeftMostCard + i*cardSpacing));
		}		
	}	
	
	//backSeat (play previous hand)
	public Hand (int[] previousIdArray){		
		cards = new ArrayList<>();	
		idArray = previousIdArray.clone();	
		
		for (int i = 0; i < 25; i++)
			addCard(new Card(previousIdArray[i]));
	}
	
	//driverSeat (always)
	public Hand (Hand excludeHand1, Hand excludeHand2){
		cards = new ArrayList<>();	
		idArray = new int[25];
		createFullDeck();
		
		for (int i = 0; i < excludeHand1.getHandSize(); i++){			
			this.removeCard(excludeHand1.getCard(i));
		}
		
		for (int i = 0; i < excludeHand2.getHandSize(); i++){			
			this.removeCard(excludeHand2.getCard(i));
		}
		
		sort();		
		
		for (int i = 0; i < 25; i++)
			idArray[i] = cards.get(i).getId();
	}	
	
	//backSeat (random hand after taking out my hand from deck)
	public Hand (Hand excludeHand){
		cards = new ArrayList<>();	
		idArray = new int[25];
		Hand deck = new Hand();
		deck.createFullDeck();
						
		for (int i = 0; i < excludeHand.getHandSize(); i++){			
			deck.removeCard(excludeHand.getCard(i));
		}
		
		deck.shuffle();
		
		for (int i = 0; i < 25; i++){	
			cards.add(deck.getCard(i));
			idArray[i] = deck.getCard(i).getId();
		}
		
		sort();		
		Arrays.sort(idArray);
	}	
	
	//backSeat (selected trump and backup distribution)
	public Hand(Hand excludeHand, int trumpSuit, int backupSuit, int trumpCount, int backupCount) {
		cards = new ArrayList<>();	
		idArray = new int[25];
		Hand deck = new Hand();
		deck.createFullDeck();
						
		for (int i = 0; i < excludeHand.getHandSize(); i++)			
			deck.removeCard(excludeHand.getCard(i));
		
		deck.shuffle();
		int trumpC = trumpCount;
		int backupC = backupCount;
		int otherCards = 25 - trumpCount - backupCount;
		
		for (int i = 0; i < 50; i++){
			
			int suit = deck.getCard(i).getSuitCode();
			if(trumpC > 0 && trumpSuit == suit){
				trumpC--;				
			} else if(backupC > 0 && backupSuit == suit){
				backupC--;				
			} else if(otherCards > 0 && suit != trumpSuit && suit != backupSuit ){
				otherCards--;
			} else{
				continue;
			}		
			
			cards.add(deck.getCard(i));
		}
		
		sort();		

		for (int i = 0; i < 25; i++)
			idArray[i] = cards.get(i).getId();

	}
	
	public void createFullDeck() {
		for (int j = 1; j <= 79; j += 4) {
			for (int i = 1; i <= 4; i++)
				addCard(new Card(j));
		}
	}
	
	public void createFullCardSet() {		
		for(int i = 1; i <= 79; i += 4)			
			addCard(new Card(i));		
	}

	public void shuffle() {		
		ArrayList<Card> tmpHand = new ArrayList<>();
		Random random = new Random();
		int randomCardIndex;
		int originalSize = cards.size();
		
		for (int i = 0; i < originalSize; i++) {
			randomCardIndex = random.nextInt(cards.size());
			tmpHand.add(cards.get(randomCardIndex));
			cards.remove(randomCardIndex);
		}
		
		cards = tmpHand;
	}	

	public String toString() {
		String cardListOutput = "";
		
		for (Card aCard : cards) {
			cardListOutput += Integer.toString(aCard.getId()) + ", ";
		}
		
		return cardListOutput;
	}
	
	public void addCard(Card addCard){		
		cards.add(addCard);
	}
	
	public void addCard(int idNumber, double xCoord){		
		cards.add(new Card(idNumber, xCoord));
	}

	public void removeCard(int index){
		cards.remove(index);
	}
	
	public boolean removeCard(Card card){
		if(cards.remove(card))
			return true;

		return false;			
	}
	
	public Card getCard(int index){
		return cards.get(index);
	}	
	
	public int getHandSize(){
		return cards.size();
	}
	
	private void sort(){		
		Collections.sort(cards);
	}
	
} // end of class

