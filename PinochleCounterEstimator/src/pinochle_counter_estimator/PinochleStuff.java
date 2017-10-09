package pinochle_counter_estimator;
import javafx.application.Application;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public abstract class PinochleStuff extends Application{

	public static final int SPADES = 1;
	public static final int DIAMONDS = 2;
	public static final int CLUBS = 3;
	public static final int HEARTS = 4;	
	public static final int AS =  1, TS =  5, KS =  9, QS = 13, JS = 17,
	         AD = 21, TD = 25, KD = 29, QD = 33, JD = 37,
	         AC = 41, TC = 45, KC = 49, QC = 53, JC = 57,
	         AH = 61, TH = 65, KH = 69, QH = 73, JH = 77;
	
	public static int boss;
	public static int trumpCode;
	public static int tableCount;
	public static int suitOfTableCard1;
	public static int myHandSize = 25;
	public static int AILevel = 3;	
	public static int aceComeBackCard;
	public static int tricksPlayed;
	public static int spadesCount, diamondsCount, clubsCount, heartsCount;	
	public static int countersInMeldSeatsStack;
	public static int legalArrayCount;
	public static int distribution;	
	public static int bestEstimation;
	public static int trumpOfBestEstimation;
	public static double bossCardDouble;
	public static double tableCard1, tableCard2, tableCard3;	
		
	public static int[] driverSeatSuitsLowestToHighest = new int[4];
	public static int[] driverSeatSuitsCountsLowestToHighest = new int[4];
	public static int[] backSeatSuitsLowestToHighest = new int[4];
	public static int[] backSeatSuitsCountsLowestToHighest = new int[4];	
	public static int[] legalArray = new int[25];	
	public static int[] subsetOfHandChooseFive = new int[5];
	public static int[] optimalDiscards = new int[5];
	
	//index 0, 1, 2 = meldSeat, backSeat, driverSeat 	
	public static boolean[] trumpingSpades = new boolean[3];
	public static boolean[] trumpingDiamonds = new boolean[3];
	public static boolean[] trumpingClubs = new boolean[3];
	public static boolean[] trumpingHearts = new boolean[3];
	
	public static Random random = new Random();
	public static Hashtable<String, ArrayList<Integer>> relKnowledge = new Hashtable<>();		
	public static Queue<Double> playQueue = new LinkedList<Double>();		
	
	public static int howManyOfThisCardHasBeenPlayed(int card) {
		int count = 0;

		for (Integer playedCard : relKnowledge.get("nonMelders_playedCards")) {
			if (playedCard == card)
				count++;
		}
		
		return count;
	}
	
	public static int howManyOfThisCardIsThereInHand(int[] legalCards, int legCardsSize, int card) {
		int count = 0;

		for (int i = 0; i < legCardsSize; i++) {
			if (legalCards[i] == card)
				count++;
		}

		return count;
	}

	public static int howManySameSuitLowerRankedCardsInHand(int[] legalCards, int legCardsSize, int card) {
		int count = 0;

		for (int i = 0; i < legCardsSize; i++) {
			if (areTheseCardsTheSameSuit(legalCards[i], card) && (legalCards[i] > card))
				count++;
		}

		return count;
	}
	
	public static boolean isCardCurrentlyHighestInItsSuit(int card) {
		if (isThisAnAceOfAnySuit(card))
			return true;

		int suitConstant = 20 * (getSuitCodeOfThisCard(card) - 1);

		if (howManyOfThisCardHasBeenPlayed(AS + suitConstant) < 4)
			return false;
		else if (card == TS + suitConstant)
			return true;
		else if (howManyOfThisCardHasBeenPlayed(TS + suitConstant) < 4)
			return false;
		else if (card == KS + suitConstant)
			return true;
		else if (howManyOfThisCardHasBeenPlayed(KS + suitConstant) < 4)
			return false;
		else if (card == QS + suitConstant)
			return true;
		else if (howManyOfThisCardHasBeenPlayed(QS + suitConstant) < 4)
			return false;
		else
			return true;

	}

	public static boolean isThisSeatTrumpingThisSuit(String seat, int suitCode){		
		int index;
		boolean returnedBool;

		if (seat.equals("meldSeat"))
			index = 0;
		else if (seat.equals("backSeat"))
			index = 1;
		else // i.e.seat.equals("driverSeat")
			index = 2;
		
		if (suitCode == 1)
			returnedBool = trumpingSpades[index];
		else if (suitCode == 2)
			returnedBool = trumpingDiamonds[index];
		else if (suitCode == 3)
			returnedBool = trumpingClubs[index];
		else //i.e. suitCode = 4
			returnedBool = trumpingHearts[index];
		
		return returnedBool;		
	}
	
	public static int pay (int[] legalCards, int legCardsSize) {
		int playCard = 0;
		int maxValue = -1;
		int testValue;

		for (int i = legCardsSize - 1; i >= 0; i--) {

			if (isThisANonCounter(legalCards[i])
					|| (isThisAnAceOfAnySuit(legalCards[i]) && isThisATrumpOfAnyValue(legalCards[i]))) {
				testValue = 0;

			} else if (isThisAKingOfAnySuit(legalCards[i])){
				testValue = 3;
			} else if (isThisATenOfAnySuit(legalCards[i])){
				testValue = 2;
			} else{// it's a non-trump ace
				testValue = 1;
			}

			if (testValue > maxValue) {
				maxValue = testValue;
				playCard = legalCards[i];
			}
		}

		return playCard;
	}
	
	public static int throwOff (int[] legalCards, int legCardsSize, boolean isJackPriority) {
		int playCard = 0;
		int maxValue = -100;		
		int priority = isJackPriority == true? 17 : 13;

		for (int i = 0; i < legCardsSize; i++) {
			int testValue = legalCards[i] % 20;
			
			if (testValue == priority)
				return legalCards[i];

			if (testValue > maxValue) {
				maxValue = testValue;
				playCard = legalCards[i];
			}
		}
		
		return playCard;
	}
	
	public static int getBestAceComeBackCard(int[] legalCards, int legCardsSize){		
		/*
		 * The logic here is as follows: we want to consider only returning a
		 * same-suit card as the ace come-back, and in the following desired
		 * order (for optimal effect): 1. king 2. ten 3. queen 4. jack. If none
		 * of these cards exists, then we want to return a zero.
		 */
		
		int max = 0;
		int maxCard = 0;
		
		for (int i = 0; i < legCardsSize; i++) {
			if (areTheseCardsTheSameSuit(legalCards[i], aceComeBackCard)) {

				if (isThisAKingOfAnySuit(legalCards[i]))
					return legalCards[i];

				if (isThisATenOfAnySuit(legalCards[i])) {
					maxCard = legalCards[i];
					max = 40;
					
				} else if (isThisAnAceOfAnySuit(legalCards[i])) {
					if (max < 30) {
						maxCard = legalCards[i];
						max = 30;
					}
				} else if (isThisAQueenOfAnySuit(legalCards[i])) {
					if (max < 20) {
						maxCard = legalCards[i];
						max = 20;
					}
				} else {// it's a jack
					if (max < 10) {
						maxCard = legalCards[i];
						max = 10;
					}
				}
			}
		}

		return maxCard;
	}
	
	public static int induceAceComeBackIfApplicable(int[] legalCards, int legCardsSize, Sequence sequenceOfBoss,
			int partnersCard, Sequence sequOfMeldSeat) {
		if (sequenceOfBoss == sequOfMeldSeat)
			return 0;

		if (isThisAnAceOfAnySuit(partnersCard) && getSuitCodeOfThisCard(partnersCard) != trumpCode) {

			int acesInHand = howManyOfThisCardIsThereInHand(legalCards, legCardsSize, partnersCard);

			if (acesInHand > 0 && (acesInHand + howManyOfThisCardHasBeenPlayed(partnersCard) == 4)) {
				if (acesInHand > 1) {
					aceComeBackCard = partnersCard;
				}

				return partnersCard;
			}
		}

		return 0;
	}
	
	public static int checkIfAceComeBackIsInduced(int[] legalCards, int legCardsSize){		
		if(aceComeBackCard != 0){			
			int comeBackCard = getBestAceComeBackCard(legalCards, legCardsSize);			
			aceComeBackCard = 0;			
			return comeBackCard;
		}		

		return 0;
	}
	
	public static int getCardInPriorityOf_ATJQK(int[] legalCards, int legCardsSize) {
		int max = 0;
		int maxCard = 0;

		for (int i = 0; i < legCardsSize; i++) {
			if (isThisAnAceOfAnySuit(legalCards[i]))
				return legalCards[i];

			if (isThisATenOfAnySuit(legalCards[i])) {
				maxCard = legalCards[i];
				max = 30;
				
			} else if (isThisAJackOfAnySuit(legalCards[i])) {
				if (max < 20) {
					maxCard = legalCards[i];
					max = 20;
				}
			} else if (isThisAQueenOfAnySuit(legalCards[i])) {
				if (max < 10) {
					maxCard = legalCards[i];
					max = 10;
				}
			} else {// i.e. it's an king
				if (max == 0) {
					maxCard = legalCards[i];
				}
			}
		}
		
		return maxCard;
	}
	
	public static int getCardInPriority_ATKQJ_OfThisSuit(int[] legalCards, int legCardsSize, int suitCode){
		int max = 0;
		int maxCard = 0;

		for (int i = 0; i < legCardsSize; i++) {
			if (getSuitCodeOfThisCard(legalCards[i]) == suitCode) {

				if (isThisAnAceOfAnySuit(legalCards[i]))
					return legalCards[i];

				if (isThisATenOfAnySuit(legalCards[i])) {
					maxCard = legalCards[i];
					max = 30;

				} else if (isThisAKingOfAnySuit(legalCards[i])) {
					if (max < 20) {
						maxCard = legalCards[i];
						max = 20;
					}
				} else if (isThisAQueenOfAnySuit(legalCards[i])) {
					if (max < 10) {
						maxCard = legalCards[i];
						max = 10;
					}
				} else {// i.e. it's an jack
					if (max == 0) {
						maxCard = legalCards[i];
					}
				}
			}
		}

		return maxCard;
	}
	
	public static int getCardInPriority_JQKTA_OfThisSuit(int[] legalCards, int legCardsSize, int suitCode){
		int max = 0;
		int maxCard = 0;

		for (int i = 0; i < legCardsSize; i++) {
			if (getSuitCodeOfThisCard(legalCards[i]) == suitCode) {

				if (isThisAJackOfAnySuit(legalCards[i]))
					return legalCards[i];

				if (isThisAQueenOfAnySuit(legalCards[i])) {
					maxCard = legalCards[i];
					max = 30;

				} else if (isThisAKingOfAnySuit(legalCards[i])) {
					if (max < 20) {
						maxCard = legalCards[i];
						max = 20;
					}
				} else if (isThisATenOfAnySuit(legalCards[i])) {
					if (max < 10) {
						maxCard = legalCards[i];
						max = 10;
					}
				} else {// i.e. it's an ace
					if (max == 0) {
						maxCard = legalCards[i];
					}
				}
			}
		}

		return maxCard;
	}
	
	public static Sequence whichSequentialPlayerWouldBeBoss(double card1, double card2, double card3, int trump){
		int card1Int = (int) card1;
		int card2Int = (int) card2;
		int card3Int = (int) card3;
		int leadCardInt = card1Int;

		if (card2Int < leadCardInt && ((card2Int - 1) / 20 + 1 == (leadCardInt - 1) / 20 + 1)
				|| (card2Int - 1) / 20 + 1 == trump && (leadCardInt - 1) / 20 + 1 != trump)
			leadCardInt = card2Int;

		if (card3Int != 0) {
			if (card3Int < leadCardInt && ((card3Int - 1) / 20 + 1 == (leadCardInt - 1) / 20 + 1)
					|| (card3Int - 1) / 20 + 1 == trump && (leadCardInt - 1) / 20 + 1 != trump)
				leadCardInt = card3Int;
		}

		if (leadCardInt == card1Int) {
			return Sequence.LEAD;
		} else if (leadCardInt == card2Int) {
			return Sequence.SECOND;
		} else {
			return Sequence.LAST;
		}
	}
		
	public static boolean isThereAtLeastOneCardOfThisSuitInHand (int[] legalCards, int legCardsSize, int suitCode){		
		for (int i = 0; i < legCardsSize; i++){
			
			if (getSuitCodeOfThisCard(legalCards[i]) == suitCode)
				return true;
		}
		
		return false;
	}
	
	public static boolean areBothTheseSeatsTrumpingThisSuit(String seat1, String seat2, int suitCode){
		if(isThisSeatTrumpingThisSuit(seat1, suitCode) && isThisSeatTrumpingThisSuit(seat2, suitCode))
			return true;
		
		return false;
	}
		
	public static int getDriverSeatCardThatBothOtherPlayersAreTrumping(int[] legalCards, int legCardsSize){				
		for (int suitCode = 1; suitCode <= 4; suitCode++) {			
			if (areBothTheseSeatsTrumpingThisSuit("meldSeat", "backSeat", suitCode)
					&& isThereAtLeastOneCardOfThisSuitInHand(legalCards, legCardsSize, suitCode)) {
				
				return getCardInPriority_ATKQJ_OfThisSuit(legalCards, legCardsSize, suitCode);
			}
		}
		
		return 0;
	}
	
	public static int getDriverSeatCardThatMeldSeatIsTrumping(int[] legalCards, int legCardsSize){				
		int bestCard = 0;
		int suitCountOfBestCard = 0;
		
		for (int suitCode = 1; suitCode <= 4; suitCode++) {
			
			if (isThisSeatTrumpingThisSuit("meldSeat", suitCode)
					&& isThereAtLeastOneCardOfThisSuitInHand(legalCards, legCardsSize, suitCode)
					&& getDriverOriginalSuitCountOfThisSuit(suitCode) > suitCountOfBestCard) {

				bestCard = getCardInPriority_JQKTA_OfThisSuit(legalCards, legCardsSize, suitCode);
				suitCountOfBestCard = getDriverOriginalSuitCountOfThisSuit(suitCode);
			}
		}
		
		return bestCard;
	}
	
	public static int getBackSeatCardThatMeldSeatIsTrumping(int[] legalCards, int legCardsSize){				
		int bestCard = 0;
		int suitCountOfBestCard = 21;// one more than the max possible
		
		for (int suitCode = 1; suitCode <= 4; suitCode++) {
			
			if (isThisSeatTrumpingThisSuit("meldSeat", suitCode)
					&& isThereAtLeastOneCardOfThisSuitInHand(legalCards, legCardsSize, suitCode)
					&& getBackSeatOriginalSuitCountOfThisSuit(suitCode) < suitCountOfBestCard) {

				bestCard = getCardInPriority_JQKTA_OfThisSuit(legalCards, legCardsSize, suitCode);
				suitCountOfBestCard = getBackSeatOriginalSuitCountOfThisSuit(suitCode);
			}
		}
		
		return bestCard;
	}
	
	public static int getDriverOriginalSuitCountOfThisSuit(int suitCode) {
		int suitCount = 0;

		for (int index = 0; index < 4; index++) {
			if (driverSeatSuitsLowestToHighest[index] == suitCode) {
				suitCount = driverSeatSuitsCountsLowestToHighest[index];		
				break;
			}
		}
		
		return suitCount;
	}
	
	public static int getBackSeatOriginalSuitCountOfThisSuit(int suitCode) {
		int suitCount = 0;

		for (int index = 0; index < 4; index++) {
			if (backSeatSuitsLowestToHighest[index] == suitCode) {
				suitCount = backSeatSuitsCountsLowestToHighest[index];		
				break;
			}
		}

		return suitCount;
	}

	public static int getBackSeatSuitCountsReverseRankingOfThisSuit(int suitCode) {
		int rank = 0;

		for (int index = 0; index < 4; index++) {
			if (backSeatSuitsLowestToHighest[index] == suitCode) {
				rank = index + 1;
				break;
			}
		}
		
		return rank;
	}
	
	public static int getDriverSeatSuitCountsReverseRankingOfThisSuit(int suitCode) {
		int rank = 0;

		for (int index = 0; index < 4; index++) {
			if (driverSeatSuitsLowestToHighest[index] == suitCode) {
				rank = index + 1;
				break;
			}
		}
		
		return rank;
	}
	
	public static int getTrumpCardInPriority_QJ (int[] legalCards, int legCardsSize){		
		for (int i = 0; i < legCardsSize; i++) {
			if (isThisATrumpOfAnyValue(legalCards[i])
					&& (isThisAQueenOfAnySuit(legalCards[i]) || isThisAJackOfAnySuit(legalCards[i])))
				return legalCards[i];
		}
		
		return 0;		
	}
	
	public static int getANonAceAndNonTrumpToTryToWalk(String seat, int[] legalCards, int legCardsSize) {
		for (int i = 0; i < legCardsSize; i++) {
			int suit = getSuitCodeOfThisCard(legalCards[i]);
			
			if (suit != trumpCode) {
				if (isCardCurrentlyHighestInItsSuit(legalCards[i]) && !isThisAnAceOfAnySuit(legalCards[i])
						&& (!isThisSeatTrumpingThisSuit("meldSeat", suit)
								|| doesSeatKnowMeldSeatIsOutOfTrump(seat, "meldSeat"))) 
					return legalCards[i];
			}
		}

		return 0;
	}
	
	public static boolean canSecondPlayerBeatLeadCard (int leadCard, int secondCard){		
		if (whichSequentialPlayerWouldBeBoss(leadCard, secondCard, 0, trumpCode) == Sequence.SECOND)
			return true;
		
		return false;		
	}
	
	//SS = same suit
	public static boolean didSecondPlayerNotBeatLeadCardWhenTheyAreSS(int leadCard, int secondCard) {
		if (whichSequentialPlayerWouldBeBoss(leadCard, secondCard, 0, trumpCode) == Sequence.LEAD
				&& areTheseCardsTheSameSuit(leadCard, secondCard))
			return true;

		return false;
	}
    
	public static boolean didLastPlayerNotBeatBossWhenAll3CardsAreSS(int leadCard, int secondCard, int lastCard) {
		if (!(whichSequentialPlayerWouldBeBoss(leadCard, secondCard, lastCard, trumpCode) == Sequence.LAST)
				 && areTheseCardsTheSameSuit(leadCard, secondCard, lastCard)) {
			
			return true;
		}
		
		return false;
	}

	public static boolean didLastNotBeatLeadCardWhenItWasBossOnlyLeadAndLastCardAreSS(int leadCard, int secondCard,
			int lastCard) {
		if (!(whichSequentialPlayerWouldBeBoss(leadCard, secondCard, lastCard, trumpCode) == Sequence.LAST)
				&& areTheseCardsTheSameSuit(leadCard, lastCard) && !areTheseCardsTheSameSuit(leadCard, secondCard)
				&& boss == leadCard) {

			return true;
		}

		return false;
	}
	
	public static void setDriverSeatSuitCountArrays(int[] hand){
		int[] suitCounts = new int[4];
		boolean[] isThisSuitSorted = new boolean[4];
		 
		for (int i = 0; i < 25; i++) {
			switch(getSuitCodeOfThisCard(hand[i])){
			case SPADES:
				suitCounts[0]++;
				break;
			case DIAMONDS:
				suitCounts[1]++;
				break;
			case CLUBS:
				suitCounts[2]++;
				break;
			default:
				suitCounts[3]++;
			}			
		}
		
		driverSeatSuitsCountsLowestToHighest = suitCounts.clone();
		Arrays.sort(driverSeatSuitsCountsLowestToHighest);

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (driverSeatSuitsCountsLowestToHighest[i] == suitCounts[j] && isThisSuitSorted[j] == false){
					driverSeatSuitsLowestToHighest[i] = j + 1;
					isThisSuitSorted[j] = true;
					break;
				}
			}
		}
	}
	
	public static void setBackSeatSuitCountArrays(int[] hand){
		int[] suitCounts = new int[4];
		boolean[] isThisSuitSorted = new boolean[4];
		 
		for (int i = 0; i < 25; i++) {
			switch(getSuitCodeOfThisCard(hand[i])){
			case SPADES:
				suitCounts[0]++;
				break;
			case DIAMONDS:
				suitCounts[1]++;
				break;
			case CLUBS:
				suitCounts[2]++;
				break;
			default:
				suitCounts[3]++;
			}			
		}
		
		backSeatSuitsCountsLowestToHighest = suitCounts.clone();
		Arrays.sort(backSeatSuitsCountsLowestToHighest);
		
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (backSeatSuitsCountsLowestToHighest[i] == suitCounts[j] && isThisSuitSorted[j] == false){
					backSeatSuitsLowestToHighest[i] = j + 1;
					isThisSuitSorted[j] = true;
					break;
				}
			}
		}
	}
	
	public static int getAceInLogestNonTrumpSuit(int[] legalCards, int legCardsSize) {
		int index = 3;

		while (driverSeatSuitsCountsLowestToHighest[index] == driverSeatSuitsCountsLowestToHighest[3]) {

			for (int i = 0; i < legCardsSize; i++) {
				int suit = getSuitCodeOfThisCard(legalCards[i]);
				if (suit == driverSeatSuitsLowestToHighest[index] && suit != trumpCode
						&& isThisAnAceOfAnySuit(legalCards[i])) {
					return legalCards[i];
				}
			}

			index--;
		}

		return 0;
	}
	
	public static int backSeatGetTenOrKing(int[] legalCards, int legCardsSize) {
		for (int index = 3; index >= 0; index--) {
			int suit = backSeatSuitsLowestToHighest[index];
			int ace = getAceOfThisSuit(suit);
			int ten = getTenOfThisSuit(suit);
			int tensInHand = howManyOfThisCardIsThereInHand(legalCards, legCardsSize, ten);
			int acesOutThere = 4 - howManyOfThisCardIsThereInHand(legalCards, legCardsSize, ace)
					- howManyOfThisCardHasBeenPlayed(ace);

			if (suit == trumpCode || acesOutThere == 0
					|| doesSeatRuleOutHSSOfThisCardFromOtherSeat("backSeat", ten, "driverSeat"))
				continue;

			boolean isTenPriority = tensInHand - acesOutThere >= 0 ? true : false;
			int tenOrKingTest = getTenOrKingInThisSuit(legalCards, legCardsSize, suit, isTenPriority);
			if (tenOrKingTest > 0) {
				return tenOrKingTest;
			}
		}

		return 0;
	}
	
	public static int getTenOrKingInThisSuit(int[] legalCards, int legCardsSize, int suit, boolean isTenPriority) {
		if (isTenPriority) {
			for (int i = 0; i < legCardsSize; i++) {
				if (getSuitCodeOfThisCard(legalCards[i]) != suit)
					continue;

				if ((isThisATenOfAnySuit(legalCards[i]) || isThisAKingOfAnySuit(legalCards[i])))
					return legalCards[i];
			}

		} else {
			for (int i = legCardsSize - 1; i >= 0; i--) {
				if (getSuitCodeOfThisCard(legalCards[i]) != suit)
					continue;

				if ((isThisATenOfAnySuit(legalCards[i]) || isThisAKingOfAnySuit(legalCards[i])))
					return legalCards[i];
			}
		}

		return 0;
	}

	public static int getDriverVeryLongNonTrumpSuitInPriority_AKTQJ(int[] legalCards, int legCardsSize){		
		int longestNonTrumpSuit = driverSeatSuitsLowestToHighest[3];
		int longestNonTrumpSuitCount = driverSeatSuitsCountsLowestToHighest[3];
		
		if (longestNonTrumpSuit == trumpCode){
			longestNonTrumpSuit =  driverSeatSuitsCountsLowestToHighest[2];
			longestNonTrumpSuitCount = driverSeatSuitsCountsLowestToHighest[2];
		}
		
		//the 9 here is very debatable (maybe tweak later)
		if (longestNonTrumpSuitCount < 9)
			return 0;
		
		int max = 0;
		int maxCard = 0;

		for (int i = 0; i < legCardsSize; i++) {
			if (getSuitCodeOfThisCard(legalCards[i]) == longestNonTrumpSuit) {
				if (isThisAnAceOfAnySuit(legalCards[i]))
					return legalCards[i];

				if (isThisAKingOfAnySuit(legalCards[i])) {
					maxCard = legalCards[i];
					max = 30;

				} else if (isThisATenOfAnySuit(legalCards[i])) {
					if (max < 20) {
						maxCard = legalCards[i];
						max = 20;
					}
				} else if (isThisAQueenOfAnySuit(legalCards[i])) {
					if (max < 10) {
						maxCard = legalCards[i];
						max = 10;
					}
				} else {// i.e. it's an jack
					if (max == 0) 
						maxCard = legalCards[i];
				}
			}
		}

		return maxCard;
	}
	
	public static int getBackSeatDefaultCard (int[] legalCards, int legCardsSize) {
		//the values in this method are nowhere near optimal (tweak later)
		int max = 0;
		int maxCard = 0;

		for (int i = legCardsSize - 1; i >= 0; i--) {			
			int reverseSuitRank = getBackSeatSuitCountsReverseRankingOfThisSuit(getSuitCodeOfThisCard(legalCards[i]));
			int currentValue;
			
			if (getSuitCodeOfThisCard(legalCards[i]) != trumpCode) {
				
				if (isThisATenOfAnySuit(legalCards[i])){
					currentValue = 5 + 10*reverseSuitRank;					
	
				} else if (isThisAnAceOfAnySuit(legalCards[i])) {
					currentValue = 4 + 10*reverseSuitRank;			

				} else if (isThisAJackOfAnySuit(legalCards[i])) {
					currentValue = 3 + 10*reverseSuitRank;					
			
				} else if (isThisAQueenOfAnySuit(legalCards[i])) {
					currentValue = 2 + 10*reverseSuitRank;					
					
				} else {// i.e. it's a king
					currentValue = 1 + 10*reverseSuitRank;							
				}
				
				if (max < currentValue) {
					maxCard = legalCards[i];
					max = currentValue;
				}
				
			} else{//its a trump
				currentValue = 1;			
				
				if (max < currentValue) {
					maxCard = legalCards[i];
					max = currentValue;
				}
			}
		}

		return maxCard;
	}
	
	public static int getDriverSeatDefaultCard (int[] legalCards, int legCardsSize) {
		//the values in this method are not optimal (tweak later)
		int max = 0;
		int maxCard = 0;

		for (int i = 0; i < legCardsSize; i++) {			
			int reverseSuitRank = getDriverSeatSuitCountsReverseRankingOfThisSuit(getSuitCodeOfThisCard(legalCards[i]));
			int currentValue;

			if (getSuitCodeOfThisCard(legalCards[i]) != trumpCode) {

				if (isThisAKingOfAnySuit(legalCards[i])){
					currentValue = 5 + 10*reverseSuitRank;					
	
				} else if (isThisATenOfAnySuit(legalCards[i])) {
					currentValue = 4 + 10*reverseSuitRank;			

				} else if (isThisAnAceOfAnySuit(legalCards[i])) {
					currentValue = 3 + 10*reverseSuitRank;					
			
				} else if (isThisAQueenOfAnySuit(legalCards[i])) {
					currentValue = 2 + 10*reverseSuitRank;					
					
				} else {// i.e. it's a jack
					currentValue = 1 + 10*reverseSuitRank;		
					
				}
				
				if (max < currentValue) {
					maxCard = legalCards[i];
					max = currentValue;
				}
				
			} else{//its a trump
				currentValue = 1;	
				
				if (max < currentValue) {
					maxCard = legalCards[i];
					max = currentValue;
				}
			}
		}

		return maxCard;
	}
		
	public static void updateAIKnowledge(int playedCard, String seat) {
		/* if it was a meldSeatLead, make aceComeBackCard equal zero in case meldSeat
		 trumped/nullified an aceComeBack in the prior trick*/
		if (tableCount == 1 && (int) (bossCardDouble * 10) % 10 == 1) 
			aceComeBackCard = 0;

		int index;

		if (seat.equals("meldSeat"))
			index = 0;
		else if (seat.equals("backSeat"))
			index = 1;
		else // i.e.seat.equals("driverSeat")
			index = 2;

		// update which seat has trumped which suits
		if (tableCount != 0) {
			int suitOfTableCard1 = ((int) tableCard1 - 1) / 20 + 1;
			int suitOfPlayedCard = (playedCard - 1) / 20 + 1;

			if (suitOfTableCard1 != trumpCode && suitOfPlayedCard == trumpCode) {
				
				switch (suitOfTableCard1) {
				case 1:
					trumpingSpades[index] = true;
					break;
				case 2:
					trumpingDiamonds[index] = true;
					break;
				case 3:
					trumpingClubs[index] = true;
					break;
				case 4:
					trumpingHearts[index] = true;

				}
			}
		}

		updateUniversallyDeducedKnowledge(playedCard, seat);
		updateAllRelativeKnowledges(playedCard, seat);
	}

	public static void updateAllRelativeKnowledges(int playedCard, String seat){		
		relKnowledge.get("nonMelders_playedCards").add(playedCard);		
		removeThicCardFromDriverSeatRelKnowledge(playedCard, seat);
		removeThicCardFromBackSeatRelKnowledge(playedCard, seat);
		checkIfThisSeatsRelKCanDeduceMovingCardsToDiscards("driverSeat", "meldSeat");
		checkIfThisSeatsRelKCanDeduceMovingCardsToDiscards("driverSeat", "backSeat");
		checkIfThisSeatsRelKCanDeduceMovingCardsToDiscards("backSeat", "meldSeat");
		checkIfThisSeatsRelKCanDeduceMovingCardsToDiscards("backSeat", "driverSeat");
	}	
			
	public static void checkIfThisSeatsRelKCanDeduceMovingCardsToDiscards(String seat, String movingSeat) {
		String _OrDiscards = seat + "_" + movingSeat + "OrDiscards";
		int totalNumThatHaveToBeInDiscards = relKnowledge.get(seat + "_" + movingSeat + "OrDiscards").size()
				- getHandSizeOfThisSeat(movingSeat);

		if (totalNumThatHaveToBeInDiscards > 0) {
			HashSet<Integer> set = new HashSet<>();
			ArrayList<Integer> removeList = new ArrayList<Integer>();
			ArrayList<Integer> listCopy = new ArrayList<Integer>();

			for (Integer card : relKnowledge.get(_OrDiscards))
				listCopy.add(card);

			for (Integer card1 : relKnowledge.get(_OrDiscards)) {

				if (!(set.contains(card1) || (set.contains(-card1)))) {
					int nonInstancesCount = 0;

					for (Integer card2 : relKnowledge.get(_OrDiscards)) {
						if (card1 != card2 && card1 != -card2)
							nonInstancesCount++;
					}

					int numberOfCardsToMoveToDiscards = totalNumThatHaveToBeInDiscards - nonInstancesCount;

					for (int i = 0; i < numberOfCardsToMoveToDiscards; i++) {

						if (listCopy.remove((Integer) card1))
							removeList.add(card1);
						else if (listCopy.remove((Integer) (-card1)))
							removeList.add(-card1);

						if (card1 < 0)
							card1 *= -1;

						relKnowledge.get(seat + "_discards").add(card1);
					}
				}

				set.add(card1);
				set.add(-card1);
			}

			for (Integer removeCard : removeList)
				relKnowledge.get(_OrDiscards).remove(removeCard);
		}
	}
	
	public static int getHandSizeOfThisSeat(String seat) {		
		if (!seat.equals("meldSeat")){
			return relKnowledge.get(seat + "_self").size();
		}
		
		return myHandSize;
	}
		
	public static void removeThicCardFromBackSeatRelKnowledge(int playedCard, String seat) {
		if (seat.equals("backSeat")) {
			relKnowledge.get("backSeat_self").remove((Integer) playedCard);

		} else if (seat.equals("driverSeat")) {

			if (relKnowledge.get("backSeat_driverSeatOrDiscards").remove((Integer) playedCard)) {
			} else if (relKnowledge.get("backSeat_driverSeatOrDiscards").remove((Integer) (-playedCard))) {
			} else if (relKnowledge.get("backSeat_unknown").remove((Integer) playedCard)) {
			}

		} else {// if(seat.equals("meldSeat")){

			if (relKnowledge.get("backSeat_meldSeatOrDiscards").remove((Integer) playedCard)) {
			} else if (relKnowledge.get("backSeat_meldSeatOrDiscards").remove((Integer) (-playedCard))) {
			} else if (relKnowledge.get("backSeat_unknown").remove((Integer) playedCard)) {
			}
		}
	}
	
	public static void removeThicCardFromDriverSeatRelKnowledge(int playedCard, String seat) {
		if (seat.equals("driverSeat")) {
			relKnowledge.get("driverSeat_self").remove((Integer) playedCard);

		} else if (seat.equals("backSeat")) {

			if (relKnowledge.get("driverSeat_backSeatOrDiscards").remove((Integer) playedCard)) {
			} else if (relKnowledge.get("driverSeat_backSeatOrDiscards").remove((Integer) (-playedCard))) {
			} else if (relKnowledge.get("driverSeat_unknown").remove((Integer) playedCard)) {
			} 
		} else {// if(seat.equals("meldSeat")){

			if (relKnowledge.get("driverSeat_meldSeatOrDiscards").remove((Integer) playedCard)) {
			} else if (relKnowledge.get("driverSeat_meldSeatOrDiscards").remove((Integer) (-playedCard))) {
			} else if (relKnowledge.get("driverSeat_unknown").remove((Integer) playedCard)) {
			}
		}
	}
	
	/*these are things everyone can deduce. e.g. if lead player plays a club and 2nd player trumps it,
	   everyone now knows 2nd player has no clubs*/
	public static void updateUniversallyDeducedKnowledge(int playedCard, String seat) {
		if (tableCount == 1) {
			if (!areTheseCardsTheSameSuit((int) tableCard1, playedCard)) {
				updateKnowingNoneOfThisCardsSuitAreInThisSeat((int) tableCard1, seat);

				if (!isThisATrumpOfAnyValue((int) tableCard1) && !isThisATrumpOfAnyValue(playedCard))
					updateKnowingNoneOfThisCardsSuitAreInThisSeat(getAceOfTrump(), seat);

			} else if (didSecondPlayerNotBeatLeadCardWhenTheyAreSS((int) tableCard1, playedCard)) {
				updateKnowingHigherThanThisCardInSSIsNotInThisSeat((int) tableCard1, seat);
			}
		} else if (tableCount == 2) {
			if (didLastPlayerNotBeatBossWhenAll3CardsAreSS((int) tableCard1, (int) tableCard2, playedCard)) {
				updateKnowingHigherThanThisCardInSSIsNotInThisSeat(boss, seat);

			} else if (didLastNotBeatLeadCardWhenItWasBossOnlyLeadAndLastCardAreSS((int) tableCard1,
					(int) tableCard2, playedCard)) {
				updateKnowingHigherThanThisCardInSSIsNotInThisSeat(boss, seat);
			} else if (!areTheseCardsTheSameSuit((int) tableCard1, playedCard)) {
				updateKnowingNoneOfThisCardsSuitAreInThisSeat((int) tableCard1, seat);

				if (!isThisATrumpOfAnyValue((int) tableCard1) && !isThisATrumpOfAnyValue(playedCard))
					updateKnowingNoneOfThisCardsSuitAreInThisSeat(getAceOfTrump(), seat);

				if (whichSequentialPlayerWouldBeBoss((int) tableCard1, (int) tableCard2, playedCard,
						trumpCode) == Sequence.SECOND) {

					if (isThisATrumpOfAnyValue(playedCard))
						updateKnowingHigherThanThisCardInSSIsNotInThisSeat(boss, seat);
					else // isThisATrumpOfAnyValue(playedCard) == false
						updateKnowingNoneOfThisCardsSuitAreInThisSeat((int) tableCard2, seat);
				}
			}
		}
	}

	public static void updateKnowingHigherThanThisCardInSSIsNotInThisSeat(int card, String seat) {		
		int sameSuitCard = getSameSuitAceOfThisCard(card);
		
		while (sameSuitCard < card){
			removeAllOfThisCardAsPossibilityOThisSeatHaving(sameSuitCard, seat);
			sameSuitCard += 4;
		}
	}
	
	public static void updateKnowingNoneOfThisCardsSuitAreInThisSeat(int card, String seat) {
		for (int c = getSameSuitAceOfThisCard(card); c <= getSameSuitKJackOfThisCard(card); c += 4) 
			removeAllOfThisCardAsPossibilityOThisSeatHaving(c, seat);
	}
	
	public static void removeAllOfThisCardAsPossibilityOThisSeatHaving(int playedCard, String seat) {
		if (seat.equals("backSeat")) {
			// update driverSeat's relKnowledge
			if (moveAllOFThisCardFromThisSeatsRelKnowFromLoc1ToLoc2(playedCard, "driverSeat", "unknown",
					"meldSeatOrDiscards")) {
			} else if (moveAllOFThisCardFromThisSeatsRelKnowFromLoc1ToLoc2(playedCard, "driverSeat",
					"backSeatOrDiscards", "discards")) {
			}

		} else if (seat.equals("driverSeat")) {
			// update backSeat's relKnowledge
			if (moveAllOFThisCardFromThisSeatsRelKnowFromLoc1ToLoc2(playedCard, "backSeat", "unknown",
					"meldSeatOrDiscards")) {
			} else if (moveAllOFThisCardFromThisSeatsRelKnowFromLoc1ToLoc2(playedCard, "backSeat",
					"driverSeatOrDiscards", "discards")) {
			}

		} else {// (seat.equals("meldSeat"))
			// update driverSeat's relKnowledge
			if (moveAllOFThisCardFromThisSeatsRelKnowFromLoc1ToLoc2(playedCard, "driverSeat", "unknown",
					"backSeatOrDiscards")) {
			} else if (moveAllOFThisCardFromThisSeatsRelKnowFromLoc1ToLoc2(playedCard, "driverSeat",
					"meldSeatOrDiscards", "discards")) {
			}
			// update backSeat's relKnowledge
			if (moveAllOFThisCardFromThisSeatsRelKnowFromLoc1ToLoc2(playedCard, "backSeat", "unknown",
					"driverSeatOrDiscards")) {
			} else if (moveAllOFThisCardFromThisSeatsRelKnowFromLoc1ToLoc2(playedCard, "backSeat",
					"meldSeatOrDiscards",	"discards")) {
			}
		}
	}
	
	public static boolean moveAllOFThisCardFromThisSeatsRelKnowFromLoc1ToLoc2(int card, String seat, String loc1,
			String loc2) {
		int count = 0;
		String removeLocaton = seat + "_" + loc1;
		String addLocation = seat + "_" + loc2;
		boolean isLoc2AnOrDiscards = (loc2.length() >= 5 && loc2.charAt((loc2.length() - 5)) == 'O') ? true : false;
		int cardToAdd = isLoc2AnOrDiscards && relKnowledge.get(seat + "_discards").size() < 5 ? -card : card;

		// case where card goes from unkown to OrDiscards (+ to -) OR case where
		// card goes from OrDiscards to discards (+ to +)
		while (relKnowledge.get(removeLocaton).remove((Integer) card)) {
			relKnowledge.get(addLocation).add(cardToAdd);
			count++;
		}

		// case where card goes from OrDiscards to discards (- to +)
		while (relKnowledge.get(removeLocaton).remove((Integer) (-card))) {
			relKnowledge.get(addLocation).add(cardToAdd);
			count++;
		}

		if (count == 0)
			return false;
		else
			return true;
	}
	
	public static boolean doesSeatRuleOutHSSOfThisCardFromOtherSeat(String seat, int card, String otherSeat){		
		String seat_unknown_key = seat + "_unknown";
		String seat_otherSeatOrDiscards_key = seat + "_" + otherSeat + "OrDiscards";
		int testCard = card - 4;

		while(areTheseCardsTheSameSuit(card, testCard) && testCard > 0){			
			if (relKnowledge.get(seat_unknown_key).contains(testCard)
					|| relKnowledge.get(seat_otherSeatOrDiscards_key).contains(-testCard)
					|| relKnowledge.get(seat_otherSeatOrDiscards_key).contains(testCard)) {
				return false;
			}
			
			testCard -= 4;
		}		
		
		return true;
	}
	
	public static boolean doesSeatKnowMeldSeatIsOutOfTrump(String seat, String otherSeat){			
		//tweaked this method from doesSeatRuleOutHSSOfThisCardFromOtherSeat
		
		String seat_unknown_key = seat + "_unknown";
		String seat_otherSeatOrDiscards_key = seat + "_meldSeatOrDiscards";
		int testCard = getAceOfTrump() + 16;//jack of trump

		
		while(areTheseCardsTheSameSuit(getAceOfTrump(), testCard) && testCard > 0){
			
			if (relKnowledge.get(seat_unknown_key).contains(testCard)
					|| relKnowledge.get(seat_otherSeatOrDiscards_key).contains(-testCard)
					|| relKnowledge.get(seat_otherSeatOrDiscards_key).contains(testCard)) {
				return false;
			}
			
			testCard -= 4;
		}		
		
		return true;
	}	
	
	public static int getSameSuitAceOfThisCard(int card){		
		int suitCode = (card - 1) / 20 + 1;		
		return (suitCode - 1) * 20 + 1;		
	}
	
	public static int getSameSuitTenOfThisCard(int card){
		return getSameSuitAceOfThisCard(card) + 4;		
	}
	
	public static int getSameSuitKingOfThisCard(int card){
		return getSameSuitAceOfThisCard(card) + 8;		
	}
	
	public static int getSameSuitQueenOfThisCard(int card){
		return getSameSuitAceOfThisCard(card) + 12;		
	}
	
	public static int getSameSuitKJackOfThisCard(int card){
		return getSameSuitAceOfThisCard(card) + 16;		
	}
	
	public static boolean isThisAnAceOfAnySuit (int card){
		if ((card % 20) == AS)
			return true;
		
		return false;
	}
	
	public static boolean isThisATenOfAnySuit (int card){
		if ((card % 20) == TS)
			return true;
		
		return false;
	}
	
	public static boolean isThisAKingOfAnySuit (int card){
		if ((card % 20) == KS)
			return true;
		
		return false;
	}
	
	public static boolean isThisAQueenOfAnySuit (int card){
		if ((card % 20) == QS)
			return true;
		
		return false;
	}
	
	public static boolean isThisAJackOfAnySuit (int card){
		if ((card % 20) == JS)
			return true;
		
		return false;
	}
	
	public static boolean isThisATrumpOfAnyValue(int card){
		if (trumpCode == (card - 1) / 20 + 1)
			return true;
		
		return false;
	}
	
	public static boolean isThisACounter(int card){
		if(isThisANonCounter(card))
			return false;
		
		return true;
	}
	
	public static boolean isThisANonCounter(int card){		
		if(isThisAJackOfAnySuit(card) || isThisAQueenOfAnySuit(card))
			return true;
		
		return false;		
	}	
	
	public static boolean areTheseCardsTheSameSuit (int card1, int card2){
		if (((card1 - 1) / 20 + 1 ) == ((card2 - 1) / 20 + 1))
			return true;
		
		return false;
	}
	
	public static boolean areTheseCardsTheSameSuit(int card1, int card2, int card3) {
		if (areTheseCardsTheSameSuit(card1, card2) && areTheseCardsTheSameSuit(card2, card3))
			return true;

		return false;
	}
	
	public static int getSuitCodeOfThisCard(int card){		
		return (card - 1) / 20 + 1;
	}
	
	public static int getAceOfThisSuit(int suit){
		return (suit - 1) * 20 + 1;
	}
	
	public static int getTenOfThisSuit(int suit){
		return (suit - 1) * 20 + 5;
	}
	
	public static int getAceOfTrump(){		
		return (trumpCode - 1)*20 + 1;
	}
		
	public static int getAnUnprotectedHighestSuitCardIfItExists (int[] legalCards, int legCardsSize){		
		for (int i = 0; i < legCardsSize; i++){
			if (isThisAnUnprotectedHighestSuitCard(legalCards, legCardsSize, legalCards[i]))
				return legalCards[i];
		}
		
		return 0;
	}
	
	public static boolean isThisAnUnprotectedHighestSuitCard(int[] legalCards, int legCardsSize, int card) {
		if (isSomeoneTrumpingThisSuit(legalCards, legCardsSize, getSuitCodeOfThisCard(card))
				|| !isCardCurrentlyHighestInItsSuit(card)
				|| howManyOfThisCardIsThereInHand(legalCards, legCardsSize, card) + howManyOfThisCardHasBeenPlayed(card)
						+ howManySameSuitLowerRankedCardsInHand(legalCards, legCardsSize, card) >= 4)
			return false;

		return true;
	}
	
	public static boolean isSomeoneTrumpingThisSuit (int[] legalCards, int legCardsSize, int suitCode){
		if (suitCode == SPADES){
			for (int i = 0; i < 3; i++){
				if (trumpingSpades[i] == true)
					return true;				
			}
		} else if (suitCode == DIAMONDS){
			for (int i = 0; i < 3; i++){
				if (trumpingDiamonds[i] == true)
					return true;				
			}
		} else if (suitCode == CLUBS){
			for (int i = 0; i < 3; i++){
				if (trumpingClubs[i] == true)
					return true;				
			}
		} else {//HEARTS
			for (int i = 0; i < 3; i++){
				if (trumpingHearts[i] == true)
					return true;				
			}
		}
		
		return false;
	}	

	public static int backSeat(int[] legalCards, int legCardsSize, String player) {		
		int playCard;
		
		if (tableCount == 0) {
			playCard = backSeatLead(legalCards, legCardsSize);
		} else if (tableCount == 1) {
			playCard = backSeatSecond(legalCards, legCardsSize);
		} else {// if (tableCount == 2)
			playCard = backSeatLast(legalCards, legCardsSize);
		}
		
		return playCard ;
	}
	
	public static int backSeatLead(int[] legalCards, int legCardsSize) {	
		if (AILevel < 3)
			return legalCards[random.nextInt(legCardsSize)];
		
		//play unprotected aces if possible
		int testCard = getAnUnprotectedHighestSuitCardIfItExists(legalCards, legCardsSize);		
		if (testCard != 0)
			return testCard;
		
		//check for ace comeback
		int aceComeBackStatus = checkIfAceComeBackIsInduced(legalCards, legCardsSize);		
		if (aceComeBackStatus != 0)
			return aceComeBackStatus;
		
		// check if walking a non ace and is possible
		int nonAceToWalk = getANonAceAndNonTrumpToTryToWalk("backSeat", legalCards, legCardsSize);
		if (nonAceToWalk > 0) 
			return nonAceToWalk;
		
		// check for a card that meldSeat is trumping
		int meldSeatTrumingSuit = getBackSeatCardThatMeldSeatIsTrumping(legalCards, legCardsSize);
		if (meldSeatTrumingSuit > 0)
			return meldSeatTrumingSuit;
		
		int tenOrKing = backSeatGetTenOrKing(legalCards, legCardsSize);
		if (tenOrKing > 0)
			return tenOrKing;
		
		return getBackSeatDefaultCard(legalCards, legCardsSize);
		
	}
	
	public static int backSeatSecond(int[] legalCards, int legCardsSize) {
		if (AILevel < 2)
			return legalCards[random.nextInt(legCardsSize)];
				
		//if backSeat takes bossSeat
		if(whichSequentialPlayerWouldBeBoss((int)tableCard1, legalCards[0], 0, trumpCode) == Sequence.SECOND)
			return pay(legalCards, legCardsSize);		
		
		//add more conditions here later
		
		///////////////////////////// if meldSeat takes bossSeat: /////////////////////////////////////
		
		if (isThisSeatTrumpingThisSuit("driverSeat", suitOfTableCard1))
			return pay(legalCards, legCardsSize);
		//everything below here logically assumes driverSeat has not previously trumped meldSeat's card's suit
		
		//e.g. if meldSeat leads with an ace 
		if (isCardCurrentlyHighestInItsSuit((int)tableCard1)) 
			return throwOff(legalCards, legCardsSize, false);		
		
		//e.g. if meldSeat leads with a ten and backSeat knows driver has nothing higher in the same suit
		if (doesSeatRuleOutHSSOfThisCardFromOtherSeat("backSeat", (int) tableCard1, "driverSeat")) 
			return throwOff(legalCards, legCardsSize, false);		
		
		//e.g. if meldSeat leads with a ten and backSeat dosn't know if driver has an ace
		return pay(legalCards, legCardsSize);		
	}

	public static int backSeatLast(int[] legalCards, int legCardsSize) {
		if (AILevel < 1)
			return legalCards[random.nextInt(legCardsSize)];

		Sequence sequOfBoss = whichSequentialPlayerWouldBeBoss(tableCard1, tableCard2, legalCards[0], trumpCode);

		int aceComeBackSatus = induceAceComeBackIfApplicable(legalCards, legCardsSize, sequOfBoss, (int) tableCard1,
				Sequence.SECOND);
		if (aceComeBackSatus > 0)
			return aceComeBackSatus;

		if (sequOfBoss == Sequence.SECOND)
			return throwOff(legalCards, legCardsSize, false);
		else
			return pay(legalCards, legCardsSize);
	}
	
	public static int driverSeat(int[] legalCards, int legCardsSize, String player) {		
		int playCard;

		if (tableCount == 0) {
			playCard = driverSeatLead(legalCards, legCardsSize);
		} else if (tableCount == 1) {
			playCard = driverSeatSecond(legalCards, legCardsSize);
		} else {// if (tableCount == 2)
			playCard = driverSeatLast(legalCards, legCardsSize);
		}
		
		return playCard ;
	}
	
	public static int driverSeatLead(int[] legalCards, int legCardsSize) {		
		if (AILevel < 3)
			return legalCards[random.nextInt(legCardsSize)];
		
		//play unprotected aces if possible
		int testCard = getAnUnprotectedHighestSuitCardIfItExists(legalCards, legCardsSize);
		if (testCard > 0)
			return testCard;
		
		//check for ace-comeback
		int aceComeBackStatus = checkIfAceComeBackIsInduced(legalCards, legCardsSize);		
		if (aceComeBackStatus != 0)
			return aceComeBackStatus;
		
		//check for card that both other players are trumping
		int doubleTrumpingSuitCard = getDriverSeatCardThatBothOtherPlayersAreTrumping(legalCards, legCardsSize);
		if(doubleTrumpingSuitCard > 0)
			return doubleTrumpingSuitCard;
		// check if walking a non ace is possible
		int nonAceToWalk = getANonAceAndNonTrumpToTryToWalk("driverSeat", legalCards, legCardsSize);
		if(nonAceToWalk > 0)
			return nonAceToWalk;
				
		//check for a meldSeat trumped suit
		int trumpedSuitCard = getDriverSeatCardThatMeldSeatIsTrumping(legalCards, legCardsSize);
		if(trumpedSuitCard > 0)
			return trumpedSuitCard;
		
		// check for a very long suit card
		int veryLongSuitCard = getDriverVeryLongNonTrumpSuitInPriority_AKTQJ(legalCards, legCardsSize);
		if (veryLongSuitCard > 0) 
			return veryLongSuitCard;
		
		//check for an ace in longest suit
		int aceInLongSuit = getAceInLogestNonTrumpSuit(legalCards, legCardsSize);
		if(aceInLongSuit > 0)
			return aceInLongSuit;
		
		//check for queen or jack of trump
		int queenOrJackOfTrump = getTrumpCardInPriority_QJ(legalCards, legCardsSize);
		if (queenOrJackOfTrump > 0) 
			return queenOrJackOfTrump;
		
		return getDriverSeatDefaultCard(legalCards, legCardsSize);		
	}
	
	public static int driverSeatSecond(int[] legalCards, int legCardsSize) {
		if (AILevel < 2)
			return legalCards[random.nextInt(legCardsSize)];
		
		//check for ace come back
		Sequence sequOfBoss = whichSequentialPlayerWouldBeBoss(tableCard1, legalCards[0], 0, trumpCode);
		int aceComeBackSatus = induceAceComeBackIfApplicable(legalCards, legCardsSize, sequOfBoss, (int) tableCard1,
				Sequence.LAST);
		if (aceComeBackSatus > 0)
			return aceComeBackSatus;		
		
		
		if (isThisSeatTrumpingThisSuit("meldSeat", suitOfTableCard1)){			
			/*e.g. if driver knows that meldSeat has already trumped the lead-card suit and
			    driver also can't trump this time*/
			if (isThisATrumpOfAnyValue(legalCards[0]) == false)
				return throwOff(legalCards, legCardsSize, true);
			
			//temporary.. delete and add more conditions here later
			return throwOff(legalCards, legCardsSize, true);
		}
		// everything below here logically assumes meldSeat has not previously trumped backSeat's card's suit 
		
		//e.g. if backSeat played a non-trump and driver has to trump it
		if (!isThisATrumpOfAnyValue((int)tableCard1) && isThisATrumpOfAnyValue(legalCards[0]))
			return pay(legalCards, legCardsSize);
		
		//e.g. if backSeat leads with an ace 
		if (isCardCurrentlyHighestInItsSuit((int)tableCard1)) 
			return pay(legalCards, legCardsSize);
		
		//e.g. if backSeat leads with a ten and driverSeat knows meldSeat has nothing higher in the same suit
		if (doesSeatRuleOutHSSOfThisCardFromOtherSeat("driverSeat", (int) tableCard1, "meldSeat")) 
			return pay(legalCards, legCardsSize);		
		
		 /*  e.g. if backSeat leads with either a jack, queen, king and driver has to play within the same suit.
		     (note that backSeat's card cannot be an ace at this point in the logic) */		
		if (!isThisATenOfAnySuit((int) tableCard1) && areTheseCardsTheSameSuit((int) tableCard1, legalCards[0]))
			return getCardInPriorityOf_ATJQK(legalCards, legCardsSize);
		
		/*e.g. if backSeat leads with a ten and driverSeat dosn't know if meldSeat has an
		 *  ace (assumes that meldSeat does)*/
		return throwOff(legalCards, legCardsSize, true);		
	}
	
	public static int driverSeatLast(int[] legalCards, int legCardsSize) {
		if (AILevel < 1)
			return legalCards[random.nextInt(legCardsSize)];

		Sequence sequOfBoss = whichSequentialPlayerWouldBeBoss(tableCard1, tableCard2, legalCards[0], trumpCode);

		int aceComeBackSatus = induceAceComeBackIfApplicable(legalCards, legCardsSize, sequOfBoss, (int) tableCard2,
				Sequence.LEAD);		
		if (aceComeBackSatus > 0)
			return aceComeBackSatus;

		if (sequOfBoss == Sequence.LEAD)
			return throwOff(legalCards, legCardsSize, true);
		else
			return pay(legalCards, legCardsSize);
	}
	
	public static void calculateLegalArray(int array[], int n) {		
		suitOfTableCard1 = ((int) tableCard1 - 1) / 20 + 1;
		suitCounter(array, n);
		legalArrayCount = 0;
		int suitCodeOfBossCard = (boss - 1) / 20 + 1;

		if (boss == 0) {
			for (int i = 0; i < n; i++)
				legalArray[i] = array[i];

			legalArrayCount = n;
		} else if (tableCount == 2 && suitCodeOfBossCard != suitOfTableCard1
				&& numberOfCardSameAsBossSuit(array, n, suitOfTableCard1) > 0) {
			for (int i = 0; i < n; i++) {
				if (((array[i] - 1) / 20 + 1 == suitOfTableCard1)) {
					legalArray[legalArrayCount] = array[i];
					legalArrayCount++;
				}
			}
		} else if (numberOfCardSameAsBossSuit(array, n, suitCodeOfBossCard) > 0) {

			for (int i = 0; i < n; i++) {
				if (((array[i] - 1) / 20 + 1 == suitCodeOfBossCard) && (array[i] < boss)) {
					legalArray[legalArrayCount] = array[i];
					legalArrayCount++;
				}
			}

			if (legalArrayCount == 0) {
				for (int i = 0; i < n; i++) {
					if ((array[i] - 1) / 20 + 1 == suitCodeOfBossCard) {
						legalArray[legalArrayCount] = array[i];
						legalArrayCount++;
					}
				}
			}
		} else {
			if (trumpCode == 1 && spadesCount > 0 || trumpCode == 2 && diamondsCount > 0
					|| trumpCode == 3 && clubsCount > 0 || trumpCode == 4 && heartsCount > 0) {
				for (int i = 0; i < n; i++) {
					if (((array[i] - 1) / 20 + 1 == trumpCode)) {
						legalArray[legalArrayCount] = array[i];
						legalArrayCount++;
					}
				}

			} else {
				for (int i = 0; i < n; i++)
					legalArray[i] = array[i];

				legalArrayCount = n;
			}
		}
	}
	
	public static boolean isCardPlayLegal(int array[], int n, int cardId) {					
		for (int i = 0; i < legalArrayCount; i++) {			
			if (cardId == legalArray[i]) {
				myHandSize--;
				return true;
			}			
		}
		
		return false;
	}			

	public static void makeBossAndAllTableCardsZero() {
		boss = 0;
		tableCount = 0;
		tableCard1 = 0.0;
		tableCard2 = 0.0;
		tableCard3 = 0.0;	
	}	
	
	public static void resetSuitCountsToZero() {
		spadesCount = 0;
		diamondsCount = 0;
		clubsCount = 0;
		heartsCount = 0;
	}

	public static void suitCounter(int array[], int n) {
		resetSuitCountsToZero();

		for (int i = 0; i < n; i++) {

			if (array[i] >= 1 && array[i] <= 20)
				spadesCount++;
			else if (array[i] >= 1 && array[i] <= 20)
				spadesCount++;
			else if (array[i] >= 21 && array[i] <= 40)
				diamondsCount++;
			else if (array[i] >= 41 && array[i] <= 60)
				clubsCount++;
			else if (array[i] >= 61 && array[i] <= 80)
				heartsCount++;
		}
	}

	public static int numberOfCardSameAsBossSuit(int array[], int n, int bossSuit) {
		int count = 0;

		for (int i = 0; i < n; i++) {
			if ((array[i] - 1) / 20 + 1 == bossSuit)
				count++;
		}
		return count;
	}
		
	public static int losersCounter(int[] array, int[] throwAway) {		
		int losersCount = losersCounterForTrump(array, throwAway, 25);
		
		for (int suitCode = 1; suitCode <=  4; suitCode++){
			if (suitCode != trumpCode){
				losersCount += losersCounterForOutsideTrump(array, throwAway, suitCode);
			}
		}

		return losersCount;
	}
		
	public static int losersCounterForTrump(int[] array, int[] throwAway, int n) {
		int gettingPaid;		
		int payingThem;
		int acesOfTrump = 0;
		int countersOfTrumpInHand = 0;
		int nonCountersOfTrumpInHand = 0;
		
		for (int i = 0; i < 25; i++){
			if (getSuitCodeOfThisCard(array[i]) == trumpCode){
				
				if (isThisAnAceOfAnySuit(array[i]))
					acesOfTrump++;
				
				if (isThisACounter(array[i]))
					countersOfTrumpInHand++;
				else
					nonCountersOfTrumpInHand++;				
			}
		}				
		
		payingThem = 4 - acesOfTrump - nonCountersOfTrumpInHand;
		
		if (payingThem < 0)
			payingThem = 0;
		
		if (payingThem > countersOfTrumpInHand - acesOfTrump)
			payingThem = countersOfTrumpInHand - acesOfTrump;		
		
		//if you have all four aces of trump, count your tens as aces in the calculation for getting paid.
		if (acesOfTrump == 4){
			for (int i = 0; i < 25; i++){
				
				if (isThisATenOfAnySuit(array[i]) && getSuitCodeOfThisCard(array[i]) == trumpCode)
					acesOfTrump++;				
			}
		}
		
		gettingPaid = 2 * acesOfTrump + nonCountersOfTrumpInHand - 8;
		
		if (gettingPaid < 0)
			gettingPaid = 0;
		
		if (gettingPaid > 12 - countersOfTrumpInHand)
			gettingPaid = 12 - countersOfTrumpInHand;
		
		int lastTrickLosers = countersOfTrumpInHand + nonCountersOfTrumpInHand >= 9 ? 0 : 1;
		
		return 12 - countersOfTrumpInHand - gettingPaid + payingThem + lastTrickLosers;
	}

	public static int losersCounterForOutsideTrump(int[] array, int[] throwAway, int suitCode) {
		int maxlosers, manuallosers, paid;
		int aceCount = 0, countersCount = 0, nonCountersCount = 0;

		for (int i = 0; i < 25; i++) {
			if (getSuitCodeOfThisCard(array[i]) == suitCode) {

				if (isThisAnAceOfAnySuit(array[i]))
					aceCount++;

				if (isThisACounter(array[i]))
					countersCount++;
				else
					nonCountersCount++;
			}
		}

		paid = 2 * aceCount + nonCountersCount + getNumberOfThrowAwayNonCountersOfThisSuit(throwAway, suitCode) - 8;
		if (paid < 0)
			paid = 0;

		maxlosers = 12 - aceCount - paid - getNumberOfThrowAwayCountersOfThisSuit(throwAway, suitCode);
		manuallosers = nonCountersCount * 2 + (countersCount - aceCount) * 3;

		if (manuallosers > maxlosers) {
			return maxlosers;
		}

		return manuallosers;
	}
	
	public static int getNumberOfThrowAwayCountersOfThisSuit(int[] throwAwayCards, int suitCode){		
		int count = 0;
		
		for(int i = 0; i < 5; i++){
			if(isThisACounter(throwAwayCards[i]) && getSuitCodeOfThisCard(throwAwayCards[i]) == suitCode)
				count++;
		}
		
		return count;
	}
	
	public static int getNumberOfThrowAwayNonCountersOfThisSuit(int[] throwAwayCards, int suitCode){		
		int count = 0;
		
		for(int i = 0; i < 5; i++){
			if(isThisANonCounter(throwAwayCards[i]) && getSuitCodeOfThisCard(throwAwayCards[i]) == suitCode)
				count++;
		}
		
		return count;
	}
	
	public static void sort(int[] array, int n) {
		int[] tempArray = new int[n];
		
		for (int i = 0; i < n; i++)
			tempArray[i] = array[i];		
		
		 Arrays.sort(tempArray);

		for (int i = 0; i < n; i++) 
			array[i] = tempArray[i];
		
	}	

	public static void removeThisCardFromHandArray(int hand[], int handSize, int card) {
		int location = 0;
		for (int i = 0; i < handSize; i++) {
			if (card == hand[i]) {
				location = i;
				break;
			}
		}
		
		hand[location] = hand[handSize - 1];
		sort(hand, handSize);		
	}
	
	public static void placeCardOnTable(double card) {		
		if (tableCount == 0)
			tableCard1 = card;
		else if (tableCount == 1)
			tableCard2 = card;
		else
			tableCard3 = card;	
	}	
	
	public static void updateBossCard(double cardDouble) {		
		int card = (int)cardDouble;
		
		if (tableCount == 0 || (card < boss && ((card - 1) / 20 + 1 == (boss - 1) / 20 + 1)
				|| (card - 1) / 20 + 1 == trumpCode && (boss - 1) / 20 + 1 != trumpCode)){
			boss = card;
			bossCardDouble = cardDouble;
		}
	}
	
	public static void updateStack() {				
		int count = tricksPlayed == 25 ? 2 : 0;
		
		if (isThisACounter((int) tableCard1))
			count++;
		if (isThisACounter((int) tableCard2))
			count++;
		if (isThisACounter((int) tableCard3))
			count++;
		
		if ((int) (bossCardDouble * 10) % 10 == 1) {
			countersInMeldSeatsStack += count;
		}
	}
		
	public static void findOptimalDiscards(int[] hand, int[] subsetOfHand, int start, int index) {
		if (index == 5) {			
			int[] hand25Cards = getArrayAfterTakingOutSubset(hand, subsetOfHandChooseFive);
			int estimation = 10000 * (50 - losersCounter(hand25Cards, subsetOfHandChooseFive))
					+ getTieBreakerForHighestEstimation(hand25Cards, subsetOfHandChooseFive);

			if (estimation > bestEstimation){
				bestEstimation = estimation ;
				trumpOfBestEstimation = trumpCode;
				optimalDiscards = subsetOfHandChooseFive.clone();
			}
			
			return;
		}

		for (int i = start; ( i < subsetOfHand.length) && ( 5 - index <= subsetOfHand.length - i ); i++) {	
			subsetOfHandChooseFive[index] = subsetOfHand[i];	
			findOptimalDiscards(hand, subsetOfHand, i + 1, index + 1);		

			while (i + 1 < subsetOfHand.length && subsetOfHand[i] == subsetOfHand[i + 1])
				i++;			
		}		
	}
	
	public static int[] getArrayAfterTakingOutSubset(int[] array, int[] subset) {		
		int[] hand = new int[25];
		int handIndex = 0;
		int throwArrayIndex = 0;
		for (int i = 0; i < 30; i++){
			if (throwArrayIndex == 5 || subset[throwArrayIndex] != array[i]){
				hand[handIndex] =  array[i];
				handIndex++;
				
			} else{
				throwArrayIndex++;
			}
		}
		
		return hand;		
	}
	
	public static int[] getHandAfterTakingOutAllTrumpAndAces(int[] origalHand) {		
		ArrayList<Integer> nonTrumpAndNonAcesList = new ArrayList<>();

		for (int i = 0; i < 30; i++){
			if (!isThisAnAceOfAnySuit(origalHand[i]) && !isThisATrumpOfAnyValue(origalHand[i]))
				nonTrumpAndNonAcesList.add(origalHand[i]);
		}
		
		// extremely rare that this case will be true
		if (nonTrumpAndNonAcesList.size() < 5){
			nonTrumpAndNonAcesList.clear();
			for (int i = 0; i < 30; i++){				
				if (!isThisATrumpOfAnyValue(origalHand[i]))
					nonTrumpAndNonAcesList.add(origalHand[i]);										
			}
		}
		
		int[] nonTrumpAndNonAces = new int[nonTrumpAndNonAcesList.size()];
		
		for (int i = 0; i < nonTrumpAndNonAces.length; i++){
			nonTrumpAndNonAces[i] = nonTrumpAndNonAcesList.get(i);
		}
		
		return nonTrumpAndNonAces;
	}
	
	//breaks the tie when two hands have the same number of losers
	public static int getTieBreakerForHighestEstimation(int[] inHandCards, int[] discards) {	
		int value = 0;
		int lastAceObserved = AS;
		int lastAceObservedCount = 0;
		
		// give 100 points for every counter in your stack
		for (int i = 0; i < 5; i++){
			if (isThisACounter(discards[i]))
				value += 100;
		}		
		/*
		 * give 100 points for every trump, and 1 point for every ten you have
		 * in your hand. Also give additional points if those tens are backed up
		 * by aces (increases the probability that you'll walk them)
		 */
		for (int i = 0; i < 25; i++){
			if (isThisATrumpOfAnyValue(inHandCards[i]))
				value += 100;
			
			if (isThisAnAceOfAnySuit(inHandCards[i])){
				
				if (inHandCards[i] == lastAceObserved){
					lastAceObservedCount++;
				}
				else{
					lastAceObserved = inHandCards[i];
					lastAceObservedCount = 1;
				}
				
			} else if (isThisATenOfAnySuit(inHandCards[i])){
				value++;
				
				if(areTheseCardsTheSameSuit(inHandCards[i], lastAceObserved))
					value += lastAceObservedCount;
			}
		}
		
		return value;
	}
	
}// end of class




