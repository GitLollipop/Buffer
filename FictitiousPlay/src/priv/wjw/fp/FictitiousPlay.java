package priv.wjw.fp;

import java.util.List;

import priv.wjw.lrs.FindAllExactNashEquilibria;
import priv.wjw.lrs.Solution;
import priv.wjw.random.MersenneTwisterFast;

public class FictitiousPlay {

	double[] payoff1s;  //所有精确纳什均衡解中agent1的payoff组成的数组
	List<Solution> solutions;
	
	private MersenneTwisterFast random = new MersenneTwisterFast(System.currentTimeMillis());
	
	public void getAllExactNE(double[][] A,double[][] B) {
		FindAllExactNashEquilibria f=new FindAllExactNashEquilibria();
		payoff1s=f.getAllEquilibria(A, B);
		solutions=f.solutions;
	}
	
	/***
	 * 输入每个agent的初始FP信念和收益矩阵，迭代收敛到一个纳什均衡,返回每个agent的均衡收益
	 * @param belief1 agent1存储的agent2的各个动作的选择次数
	 * @param belief2 agent2存储的agent1的各个动作的选择次数
	 * @param A agent1的收益矩阵
	 * @param B agent2的收益矩阵
	 */
	public double[] findOneNashEquilibria(double[] belief1,double[] belief2,double[][] A,double[][] B,double converge) {
		double lastPayoff1=0;
		double lastPayoff2=0;
		int n=0;
		while(true) {
			n++;
			//每个agent根据自己的信念选择最佳应对（从自己的动作集合中选择使payoff最大的）
			//find best response of agent1 according to belief1
			int index1=-1;
			double maxPayoff1=Double.MIN_VALUE;
			double sum=0;
			for (int j = 0; j < belief1.length; j++) {
				sum+=belief1[j];
			}
			for (int i = 0; i < belief2.length; i++) {  //比较agent1的每个动作对应获得的payoff
				double payoff=0;
				for (int j = 0; j < belief1.length; j++) {
					payoff+=(belief1[j]/sum)*A[i][j];
				}
				if(payoff>maxPayoff1) {
					index1=i;
					maxPayoff1=payoff;
				}
			}
			
			//find best response of agent2 according to belief2
			int index2=-1;
			double maxPayoff2=Double.MIN_VALUE;
			sum=0;
			for (int j = 0; j < belief2.length; j++) {
				sum+=belief2[j];
			}
			for (int i = 0; i < belief1.length; i++) {  //比较agent1的每个动作对应获得的payoff
				double payoff=0;
				for (int j = 0; j < belief2.length; j++) {
					payoff+=(belief2[j]/sum)*B[j][i];
				}
				if(payoff>maxPayoff2) {
					index2=i;
					maxPayoff2=payoff;
				}
			}
			
			//每个agent更新自己的初始信念
			belief1[index2]=belief1[index2]+1;
			belief2[index1]=belief2[index1]+1;
			
			//*********************************************************************************
//			System.out.print("n="+n+" action1="+(index1+1)+" action2="+(index2+1)+" belief1=(");
//			for (int i = 0; i < belief1.length; i++) {
//				System.out.print(belief1[i]+" ");
//			}
//			System.out.print(") belief2=(");
//			for (int i = 0; i < belief2.length; i++) {
//				System.out.print(belief2[i]+" ");
//			}
//			System.out.println(") payoff1="+maxPayoff1+" payoff2="+maxPayoff2);
			//*********************************************************************************
			
			//判断每个agent的payoff是否收敛
			if(Math.abs(maxPayoff1-lastPayoff1)/maxPayoff1<converge&&Math.abs(maxPayoff2-lastPayoff2)/maxPayoff2<converge) {
				System.out.println("共经过"+n+"次迭代");
				return new double[] {maxPayoff1,maxPayoff2};
			}
			lastPayoff1=maxPayoff1;
			lastPayoff2=maxPayoff2;
		}
	}
	
	//根据agent1的payoff1比较FP收敛得到的纳什均衡对应哪个精确纳什均衡，精确纳什均衡解频率加1
	public void updateFrequence(double payoff) {
		int index=0;
		double minDiffer=Math.abs(payoff-payoff1s[0]);
		for (int i = 1; i < payoff1s.length; i++) {
			double differ=Math.abs(payoff-payoff1s[i]);
			if(differ<minDiffer) {
				minDiffer=differ;
				index=i;
			}
		}
		Solution solution=solutions.get(index);
		int frequence=solution.getFrequence();
		solution.setFrequence(frequence+1);
	}
	
	//随机产生初始信念并返回
	public double[] produceRandomInitialBeliefs(int num) {
		double[] belief=new double[num];
		for (int i = 0; i < belief.length; i++) {
			belief[i]=random.nextDouble()*10000;
		}
		return belief;
	}
	
	//根据随机产生的10万个初始信念，更新精确纳什均衡解的每个解出现的频率
	public void calFrequence(double[][] A,double[][] B) {
		double converge=0.0001;  //判断fp算法收敛的误差
		//所有精确纳什均衡解
		getAllExactNE(A, B);
		for (int i = 0; i < 100000; i++) {  //根据随机产生的100000种初始信念利用fp求解纳什均衡
			double[] belief1=produceRandomInitialBeliefs(A[0].length);
			double[] belief2=produceRandomInitialBeliefs(A.length);
			
			//************************************************************
			System.out.println("第"+(i+1)+"次fp:");
			System.out.print("belief1: ");
			for (int j = 0; j < belief1.length; j++) {
				System.out.print(belief1[j]+" ");
			}
			System.out.print("\nbelief2: ");
			for (int j = 0; j < belief2.length; j++) {
				System.out.print(belief2[j]+" ");
			}
			System.out.println();
			//************************************************************
			
			//根据初始信念采用fp求出一个近似纳什均衡
			double[] payoff=findOneNashEquilibria(belief1, belief2, A, B, converge);
			//根据近似纳什均衡更新对应精确纳什均衡出现的频率
			updateFrequence(payoff[0]);
		}
	}
	
	public static void main(String[] args) {
//		double[][] A= {{0,5,8},{2,6,5}};
//		double[][] B= {{4,6,7},{9,5,1}};
		
//		double[][] A={{0,8,4},{2,6,4},{4,0,0}};
//		double[][] B={{3,5,2},{6,3,5},{4,3,3}};
		
		double[][] A= {{0.0,0.0,0.0,0.0,0.0,0.0,5017.703281395293,0.0,0.0,1847.4563310228073},
				{0.0,0.0,0.0,0.0,0.0,0.0,0.0,4475.714980535099,0.0,0.0},
				{1701.7,0.0,0.0,1300.0,1350.0,0.0,0.0,0.0,0.0,0.0},
				{0.0,0.0,0.0,1925.0,0.0,0.0,0.0,0.0,1785.0,0.0},
				{6438.9591,0.0,0.0,0.0,0.0,2160.0,2520.0,0.0,0.0,2295.0},
				{0.0,2695.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0},
				{0.0,6373.07513828134,0.0,0.0,0.0,0.0,0.0,0.0,0.0,5352.810169846107},
				{0.0,0.0,0.0,2550.0,0.0,0.0,0.0,7788.99544275,4132.65,4425.0},
				{0.0,0.0,0.0,0.0,0.0,0.0,4730.85,0.0,0.0,0.0},
				{0.0,3541.85,0.0,0.0,3610.0,0.0,0.0,0.0,0.0,0.0}};
		
		double[][] B= {{0.0,0.0,0.0,0.0,0.0,0.0,17659.871792260485,0.0,0.0,7338.905879691402,7338.905879691402},
				{0.0,0.0,0.0,0.0,0.0,0.0,0.0,13459.037986158688,0.0,0.0,0.0},
				{2412.9,0.0,0.0,1680.0,2070.0,0.0,0.0,0.0,0.0,0.0,0.0},
				{0.0,0.0,0.0,1575.0,0.0,0.0,0.0,0.0,4165.0,0.0,0.0},
				{7910.9982,0.0,0.0,0.0,0.0,2860.0,2860.0,0.0,0.0,4655.0,4655.0},
				{0.0,765.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0},
				{0.0,14246.226607037876,0.0,0.0,0.0,0.0,0.0,0.0,0.0,11396.148210990143,11396.148210990143},
				{0.0,0.0,0.0,2310.0,0.0,0.0,0.0,12381.97015575,6965.85,3895.0,3895.0},
				{0.0,0.0,0.0,0.0,0.0,0.0,7242.95,0.0,0.0,0.0,0.0},
				{0.0,4469.85,0.0,0.0,2790.0,0.0,0.0,0.0,0.0,0.0,0.0}};
		FictitiousPlay fp=new FictitiousPlay();
		long start=System.currentTimeMillis();
		fp.calFrequence(A, B);
		long end=System.currentTimeMillis();
		for(Solution solution:fp.solutions) {
			System.out.println(solution.getPayoff1()+"  "+solution.getFrequence());
		}
		System.out.println("computation spends "+(end-start)/1000.0+"s");
		
		
		
//		double[] belief1=fp.produceRandomInitialBeliefs(A[0].length);
//		double[] belief2= fp.produceRandomInitialBeliefs(A.length);
//		double converge=0.0001;		
//		long start=System.currentTimeMillis();
//		double[] payoff=fp.findOneNashEquilibria(belief1, belief2, A, B, converge);
//		long end=System.currentTimeMillis();
//		
//		
//		double sum=0;
//		for (int i = 0; i < belief2.length; i++) {
//			sum+=belief2[i];
//		}
//		System.out.print("agent1: (");
//		for (int i = 0; i < belief2.length; i++) {
//			System.out.print((belief2[i]/sum)+", ");
//		}
//		System.out.println(") payoff1="+payoff[0]);
//						
//		sum=0;	
//		for (int i = 0; i < belief1.length; i++) {
//			sum+=belief1[i];
//		}	
//		System.out.print("agent2: (");
//		for (int i = 0; i < belief1.length; i++) {
//			System.out.print((belief1[i]/sum)+", ");
//		}	
//		System.out.println(") payoff2="+payoff[1]);
//		System.out.println("computation spends "+(end-start)/1000.0+"s");
//		
//		
//		fp.getAllExactNE(A, B);  //找到所有精确纳什均衡解
//		fp.updateFrequence(payoff[0]);  //根据fp收敛的纳什均衡更新所有精确纳什均衡中对应纳什均衡解出现的频率
//		for (int i = 0; i < fp.payoff1s.length; i++) {
//			System.out.println(fp.payoff1s[i]+"  "+fp.solutions.get(i).getFrequence());
//		}
//		
//		for (int i = 0; i < belief1.length; i++) {
//			System.out.print(belief1[i]+" ");
//		}
//		System.out.println();
//		for (int i = 0; i < belief2.length; i++) {
//			System.out.print(belief2[i]+" ");
//		}
	}
}
