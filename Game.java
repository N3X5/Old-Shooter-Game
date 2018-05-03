import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.text.AttributedString;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

/*
 * Made by: N3X
 * 
 * Please note that this code was not designed to be clean,
 * you may have trouble reading it.
 * 
 */

@SuppressWarnings("serial")
public class Game extends JFrame implements Runnable, ActionListener {

	private JPanel contentPane;
	public static int x=25, y=140, lastEnemyX=400,lastEnemyY=140, playerHitCounter = 0;
	public static int moveDirection=0,moveCount=0, playerHP=200;
	public static boolean hasDelayed = true, startMarker=true, playerIsHit=false, playerIsDefeated=false;
	private static Enemy enemy = new Enemy();
	public static Game game = new Game();
	public static LaserBeam beam = new LaserBeam();
	private static boolean startMoveAnimation = false, fireLaserBeam=false, madeByMarker = true, chooseDiff = false;
	private static BufferedImage bf[] = new BufferedImage[100];
	public static String playerOrient = "right";
	private static int playerMoveCounter = 0, diffHP = 0;
	private static Timer timer = new Timer(16, game);
	
	/**
	 * Launch the application. // Pre-built a frame with windowbuilder
	 */
	public static void main(String[] args) {
				try {
					try {
						
						InputStream file[] = 
						{
								//Player Textures
								game.getClass().getResourceAsStream("/images/gun-passive-right.png"),
								game.getClass().getResourceAsStream("/images/gun-passive-left.png"),
								game.getClass().getResourceAsStream("/images/gun-moving-full-right.png"),
								game.getClass().getResourceAsStream("/images/gun-moving-full-left.png"),
								//Enemy Textures
								game.getClass().getResourceAsStream("/images/enemy-gun-passive-right.png"),
								game.getClass().getResourceAsStream("/images/enemy-gun-passive-left.png"),
								game.getClass().getResourceAsStream("/images/enemy-gun-moving-full-right.png"),
								game.getClass().getResourceAsStream("/images/enemy-gun-moving-full-left.png"),
								//Laser Beam Texture
								game.getClass().getResourceAsStream("/images/LaserBeam.png"),
								//Background
								game.getClass().getResourceAsStream("/images/Background.jpg"),
								//HP bar
								game.getClass().getResourceAsStream("/images/hpbar_gold_empty.png"),
								//Enemy_Hit Textures
								game.getClass().getResourceAsStream("/images/enemy-gun-passive-left-hit.png"),
								game.getClass().getResourceAsStream("/images/enemy-gun-passive-right-hit.png"),
								//Player Hit Textures
								game.getClass().getResourceAsStream("/images/gun-passive-right-hit.png"),
								game.getClass().getResourceAsStream("/images/gun-passive-left-hit.png")
						};
						
						//Player
						bf[0] = ImageIO.read(file[0]);
						bf[1] = ImageIO.read(file[1]);
						bf[2] = ImageIO.read(file[2]);
						bf[3] = ImageIO.read(file[3]);
						//Enemy
						bf[4] = ImageIO.read(file[4]);
						bf[5] = ImageIO.read(file[5]);
						bf[6] = ImageIO.read(file[6]);
						bf[7] = ImageIO.read(file[7]);
						//LaserBeam
						bf[8] = ImageIO.read(file[8]);
						//Background
						bf[9] = ImageIO.read(file[9]);
						//HP bar
						bf[10] = ImageIO.read(file[10]);
						//Enemy_Hit
						bf[11] = ImageIO.read(file[11]);
						bf[12] = ImageIO.read(file[12]);
						//Player_Hit
						bf[13] = ImageIO.read(file[13]);
						bf[14] = ImageIO.read(file[14]);
						
					} catch (IOException e) {}
					new Thread(enemy).start();
					game.setVisible(true);
					timer.setInitialDelay(0);
					timer.start();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
	}
	
	@Override
	public void paint(Graphics g){
		
		if(Enemy.enemyHP<=0 && !playerIsDefeated)
			Enemy.isDefeated = true;
		if(playerHP<=0 && !Enemy.isDefeated)
			playerIsDefeated = true;
		
		Graphics2D g2d = (Graphics2D) g;
	    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
	    
		if(!startMarker && !playerIsDefeated && !Enemy.isDefeated){
			
				g.clearRect(0, 0, 10000, 10000);
				g.drawImage(bf[9], 0, 0, getWidth(), getHeight(), this);
				
				AttributedString enemyHP = new AttributedString("Your Health:");
	            enemyHP.addAttribute(TextAttribute.FONT, new Font("Impact", Font.PLAIN, 15));
	            enemyHP.addAttribute(TextAttribute.FOREGROUND, new Color(43, 207, 73));
				g.drawString(enemyHP.getIterator(), 172, 46); 
			
				g2d.drawImage(bf[10], 105, 50, 200, 20, this);
				g2d.setColor(Color.red);
				g2d.fillRect(128, 56, (int)(155.0f*(playerHP/200.0f)), 8);
				g2d.setColor(Color.BLACK);
				
				if(!Enemy.isHit)
					g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
				else{
					Enemy.enemyHitCounter++;
					Enemy.hpOpacity-=0.005f;
					g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Enemy.hpOpacity));
					if(Enemy.enemyHitCounter==80){
						Enemy.isHit=false;
						Enemy.enemyHitCounter=0;
						Enemy.hpOpacity=1.0f;
					}
				}
				
				if(playerIsHit){
					playerHitCounter++;
					if(playerHitCounter==20){
						playerIsHit = false;
						playerHitCounter = 0;
					}
				}
				
				g2d.setColor(Color.red);
				g2d.drawImage(bf[10], lastEnemyX-9, lastEnemyY-15, 35, 12, this);
				g2d.fillRect(lastEnemyX-9+4, lastEnemyY-15+3, (int)(27.0f * (Enemy.enemyHP/(float)(diffHP))), 5);
				g2d.setColor(Color.black);
				
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
				
					if(!Game.startMoveAnimation){
						playerMoveCounter=0;
							if(playerIsHit)
								g.drawImage(bf[playerOrient.equals("right")?13:14], x, y, 17, 33, this);
							else
								g.drawImage(bf[playerOrient.equals("right")?0:1], x, y, 17, 33, this);
					}
					else{
						playerMoveCounter++;
						g.drawImage(bf[playerOrient.equals("right")?2:3], x-4, y, 27, 33, this);
						if(playerMoveCounter==5)
							Game.startMoveAnimation=false;
					}
				
					
				if(LaserBeam.playerHasFiredBeam || LaserBeam.enemyHasFiredBeam){
					if(LaserBeam.playerHasFiredBeam)
						g.drawImage(bf[8], LaserBeam.playerBeamX, LaserBeam.playerBeamY, 30, 4, this);
					if(LaserBeam.enemyHasFiredBeam)
						g.drawImage(bf[8], LaserBeam.enemyBeamX, LaserBeam.enemyBeamY, 30, 4, this);
				}
				
				if(fireLaserBeam&&!LaserBeam.playerHasFiredBeam){
					beam.fireLaserBeam(x, y, "type.player");
					g.drawImage(bf[8], LaserBeam.playerBeamX, LaserBeam.playerBeamY, 30, 4, this);
					fireLaserBeam=false;
				}
				
				if(Enemy.fireLaserBeam&&!LaserBeam.enemyHasFiredBeam){
					beam.fireLaserBeam(lastEnemyX, lastEnemyY, "type.enemy");
					g.drawImage(bf[8], LaserBeam.enemyBeamX, LaserBeam.enemyBeamY, 30, 4, this);
					Enemy.fireLaserBeam=false;
				}
				
				
				if(Enemy.hasDelayed){
					if(Enemy.enemyMoveCounter==0){
					int[] XY = enemy.genAI();
						
					lastEnemyX = XY[0];
					lastEnemyY = XY[1];
					}
						if(!Enemy.startMoveAnimation){
							if(!Enemy.isHit||Enemy.enemyHitCounter>20)
								g.drawImage(bf[Enemy.enemyOrient.equals("right")?4:5], lastEnemyX, lastEnemyY, 17, 33, this);
							else
								g.drawImage(bf[Enemy.enemyOrient.equals("right")?12:11], lastEnemyX, lastEnemyY, 17, 33, this);
							Enemy.enemyMoveCounter=0;
						}
						else{
							Enemy.enemyMoveCounter++;
							g.drawImage(bf[Enemy.enemyOrient.equals("right")?6:7], lastEnemyX-4, lastEnemyY, 27, 33, this);
							if(Enemy.enemyMoveCounter==5)
								Enemy.startMoveAnimation=false;
						}
					if(Enemy.enemyMoveCounter==5||!Enemy.startMoveAnimation){
						Enemy.hasDelayed=false;
						new Thread(enemy).start();
					}
				}
				else{
					if(!Enemy.startMoveAnimation)
						if(!Enemy.isHit||Enemy.enemyHitCounter>20)
							g.drawImage(bf[Enemy.enemyOrient.equals("right")?4:5], lastEnemyX, lastEnemyY, 17, 33, this);
						else
							g.drawImage(bf[Enemy.enemyOrient.equals("right")?12:11], lastEnemyX, lastEnemyY, 17, 33, this);
					else{
							g.drawImage(bf[Enemy.enemyOrient.equals("right")?6:7], lastEnemyX-4, lastEnemyY, 27, 33, this);
							Enemy.startMoveAnimation=false;
					}
				}
				
		}
		else if(startMarker){
			
			if(madeByMarker){
			AttributedString madeMSG = new AttributedString("Made by:");
			madeMSG.addAttribute(TextAttribute.FONT, new Font("Arial", Font.BOLD, 25));
			madeMSG.addAttribute(TextAttribute.FOREGROUND, Color.RED);
			
			AttributedString byMSG = new AttributedString("N3X");
			byMSG.addAttribute(TextAttribute.FONT, new Font("Arial", Font.BOLD, 25));
			byMSG.addAttribute(TextAttribute.FOREGROUND, new Color(48, 93, 242));
			
			for(float f=0.00f; f<1.00f;f+=0.01f){
				g2d.clearRect(0, 0, 10000, 10000);
				
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
				g2d.fillRect(0, 0, 1000, 1000);
				
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, f));
				
				g.drawString(madeMSG.getIterator(), 156, 148);	
				
				
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {}
			}
			
			for(float f=0.0f; f<1.00f;f+=0.01f){
				g2d.clearRect(0, 0, 10000, 10000);
				
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
				
				g2d.fillRect(0, 0, 1000, 1000);
				g.drawString(madeMSG.getIterator(), 156, 148);	
				
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, f));
				
				g.drawString(byMSG.getIterator(), 184, 183);	
				
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {}
			}
			
			try {
				Thread.sleep(2500);
			} catch (InterruptedException e1) {}
			
			madeByMarker = false;
			chooseDiff = true;
			}
			
			if(chooseDiff){
				g.clearRect(0, 0, 10000, 10000);
				g.drawImage(bf[9], 0, 0, getWidth(), getHeight(), this);
				
				AttributedString startMSG = new AttributedString("Choose the difficulty level:");
	            startMSG.addAttribute(TextAttribute.FONT, new Font("Arial", Font.BOLD, 25));
	            startMSG.addAttribute(TextAttribute.FOREGROUND, new Color(212, 255, 0));
				g.drawString(startMSG.getIterator(), 65, 168);
				
				AttributedString optionsMSG = new AttributedString("Easy (1) Medium (2) Hard (3) Insane (4)");
				optionsMSG.addAttribute(TextAttribute.FONT, new Font("Arial", Font.BOLD, 17));
	            optionsMSG.addAttribute(TextAttribute.FOREGROUND, new Color(40, 235, 212));
				g.drawString(optionsMSG.getIterator(), 65, 204);
				
				return;
			}
			
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
			
			for(int startCounter=3;startCounter>0;startCounter--){
				g.clearRect(0, 0, 10000, 10000);
				g.drawImage(bf[9], 0, 0, getWidth(), getHeight(), this);
				AttributedString startMSG = new AttributedString("Starting game in " + startCounter + (startCounter!=1?" seconds...":" second..."));
	            startMSG.addAttribute(TextAttribute.FONT, new Font("Arial", Font.BOLD, 25));
	            startMSG.addAttribute(TextAttribute.FOREGROUND, Color.ORANGE);
				g.drawString(startMSG.getIterator(), 55, 168);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {}
			}
			g.clearRect(0, 0, 10000, 10000);
			g.drawImage(bf[9], 0, 0, getWidth(), getHeight(), this);
			AttributedString startMSG = new AttributedString("Prepare to fight!");
            startMSG.addAttribute(TextAttribute.FONT, new Font("Impact", Font.BOLD, 25));
            startMSG.addAttribute(TextAttribute.FOREGROUND, Color.red);
			g.drawString(startMSG.getIterator(), 130, 168);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {}
			startMarker=false;
		}
		else{
			g.clearRect(0, 0, 10000, 10000);
			g.drawImage(bf[9], 0, 0, getWidth(), getHeight(), this);
			
			if(chooseDiff){
				g.clearRect(0, 0, 10000, 10000);
				g.drawImage(bf[9], 0, 0, getWidth(), getHeight(), this);
				
				AttributedString startMSG = new AttributedString("Choose the difficulty level:");
	            startMSG.addAttribute(TextAttribute.FONT, new Font("Arial", Font.BOLD, 25));
	            startMSG.addAttribute(TextAttribute.FOREGROUND, new Color(212, 255, 0));
				g.drawString(startMSG.getIterator(), 65, 168);
				
				AttributedString optionsMSG = new AttributedString("Easy (1) Medium (2) Hard (3) Insane (4)");
				optionsMSG.addAttribute(TextAttribute.FONT, new Font("Arial", Font.BOLD, 17));
	            optionsMSG.addAttribute(TextAttribute.FOREGROUND, new Color(40, 235, 212));
				g.drawString(optionsMSG.getIterator(), 65, 204);
				
				return;
			}
			
			if(playerIsDefeated){
				AttributedString endMSG = new AttributedString("You have been defeated!");
	            endMSG.addAttribute(TextAttribute.FONT, new Font("Arial", Font.BOLD, 25));
	            endMSG.addAttribute(TextAttribute.FOREGROUND, Color.RED);
				g.drawString(endMSG.getIterator(), 75, 168);
				
				AttributedString optionsMSG = new AttributedString("Play Again (1)  Change Difficulty (2)  Quit (3)");
				optionsMSG.addAttribute(TextAttribute.FONT, new Font("Arial", Font.BOLD, 17));
	            optionsMSG.addAttribute(TextAttribute.FOREGROUND, new Color(245, 76, 98));
				g.drawString(optionsMSG.getIterator(), 45, 204);
			}
			else if(Enemy.isDefeated){
				AttributedString endMSG = new AttributedString("You have won!");
	            endMSG.addAttribute(TextAttribute.FONT, new Font("Arial", Font.BOLD, 25));
	            endMSG.addAttribute(TextAttribute.FOREGROUND, Color.GREEN);
				g.drawString(endMSG.getIterator(), 125, 168);
				
				AttributedString optionsMSG = new AttributedString("Play Again (1)  Change Difficulty (2)  Quit (3)");
				optionsMSG.addAttribute(TextAttribute.FONT, new Font("Arial", Font.BOLD, 17));
	            optionsMSG.addAttribute(TextAttribute.FOREGROUND, new Color(99, 255, 154));
				g.drawString(optionsMSG.getIterator(), 45, 204);
			}
		}
	}
	
	public void update(Graphics g){
		paint(g);
	}
	
	/**
	 * Create the frame.
	 */
	public Game() {
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 296);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent key) {
				
				if(hasDelayed&&!startMarker&&!chooseDiff){
				
				if(key.getKeyCode()==49 && (Enemy.isDefeated || playerIsDefeated)){
					resetGame();
				}
				
				if(key.getKeyCode()==50)
					chooseDiff = true;
				
				if(key.getKeyCode()==51 && (Enemy.isDefeated || playerIsDefeated))
					System.exit(0);
				
				if(Enemy.isDefeated || playerIsDefeated)
					return;
				
				if(key.getKeyCode()==40&&(Game.x-lastEnemyX>12||Game.x-lastEnemyX<-12||(lastEnemyY-y>40||lastEnemyY-y<-40))){
					if(y!=260)
						y+=8;
				}
				if(key.getKeyCode()==39&&(!(Game.x-lastEnemyX<-20||Game.x-lastEnemyX>0||(lastEnemyY-y>=40||lastEnemyY-y<=-40))?playerOrient.equals("left"):true)){
					if(x!=425&&playerOrient.equals("right")){
						x+=8;
						startMoveAnimation=true;
					}
					else
						playerOrient="right";
				}
				if(key.getKeyCode()==38&&(Game.x-lastEnemyX>12||Game.x-lastEnemyX<-12||(lastEnemyY-y>40||lastEnemyY-y<-40))){
					if(y!=52)
						y-=8;
				}
				if(key.getKeyCode()==37&&(!(Game.x-lastEnemyX>20||Game.x-lastEnemyX<0||(lastEnemyY-y>=40||lastEnemyY-y<=-40))?playerOrient.equals("right"):true)){
					if(x!=9&&playerOrient.equals("left")){
						x-=8;
						startMoveAnimation=true;
					}
					else
						playerOrient="left";
				}
				if(key.getKeyCode()==32&&!LaserBeam.playerHasFiredBeam){
					fireLaserBeam=true;
				}
				hasDelayed=false;
				new Thread(new Game()).start();
			}
				else if(chooseDiff){
					
					if(key.getKeyCode()==49){
						Enemy.enemyDelay = 400;
						Enemy.enemyHP = 300;
						
						diffHP = 300;
						
						chooseDiff = false;
						
						resetGame();
					}
					else if(key.getKeyCode()==50){
						Enemy.enemyDelay = 200;
						Enemy.enemyHP = 450;
						
						diffHP = 450;
						
						chooseDiff = false;
						
						resetGame();
					}
					else if(key.getKeyCode()==51){
						Enemy.enemyDelay = 100;
						Enemy.enemyHP = 600;
						
						diffHP = 600;
						
						chooseDiff = false;
						
						resetGame();
					}
					else if(key.getKeyCode()==52){
						Enemy.enemyDelay = 50;
						Enemy.enemyHP = 750;

						diffHP = 750;
						
						chooseDiff = false;
						
						resetGame();
					}
				}
		}
			});
		
	}

	private void resetGame(){
		
		playerHP = 200;
		playerIsHit = false;
		startMoveAnimation = false;
		playerMoveCounter = 0;
		fireLaserBeam = false;
		playerOrient = "right";
		playerIsHit = false;
		
		x=25;
		y=140;
		
		Enemy.enemyHP = diffHP;
		Enemy.enemyHitCounter = 0;
		Enemy.enemyMoveCounter = 0;
		Enemy.enemyOrient = "left";
		Enemy.fireLaserBeam = false;
		Enemy.hpOpacity = 1.0f;
		Enemy.startMoveAnimation = false;
		Enemy.isHit = false;
		
		lastEnemyX = 400;
		lastEnemyY = 140;
		Enemy.x = 400;
		Enemy.y = 140;
		
		playerIsDefeated = false;
		Enemy.isDefeated = false;
	}
	
	@Override
	public void run() {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {}
		hasDelayed=true;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		repaint();
		}
	}
