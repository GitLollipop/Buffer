package priv.wjw.fp;

import java.util.List;

import priv.wjw.lrs.FindAllExactNashEquilibria;
import priv.wjw.lrs.Solution;
import priv.wjw.random.MersenneTwisterFast;

public class FictitiousPlay {

	double[] payoff1s;  //���о�ȷ��ʲ�������agent1��payoff��ɵ�����
	List<Solution> solutions;
	
	private MersenneTwisterFast random = new MersenneTwisterFast(System.currentTimeMillis());
	
	public void getAllExactNE(double[][] A,double[][] B) {
		FindAllExactNashEquilibria f=new FindAllExactNashEquilibria();
		payoff1s=f.getAllEquilibria(A, B);
		solutions=f.solutions;
	}
	
	/***
	 * ����ÿ��agent�ĳ�ʼFP�����������󣬵���������һ����ʲ����,����ÿ��agent�ľ�������
	 * @param belief1 agent1�洢��agent2�ĸ���������ѡ�����
	 * @param belief2 agent2�洢��agent1�ĸ���������ѡ�����
	 * @param A agent1���������
	 * @param B agent2���������
	 */
	public double[] findOneNashEquilibria(double[] belief1,double[] belief2,double[][] A,double[][] B,double converge) {
		double lastPayoff1=0;
		double lastPayoff2=0;
		int n=0;
		while(true) {
			n++;
			//ÿ��agent�����Լ�������ѡ�����Ӧ�ԣ����Լ��Ķ���������ѡ��ʹpayoff���ģ�
			//find best response of agent1 according to belief1
			int index1=-1;
			double maxPayoff1=Double.MIN_VALUE;
			double sum=0;
			for (int j = 0; j < belief1.length; j++) {
				sum+=belief1[j];
			}
			for (int i = 0; i < belief2.length; i++) {  //�Ƚ�agent1��ÿ��������Ӧ��õ�payoff
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
			for (int i = 0; i < belief1.length; i++) {  //�Ƚ�agent1��ÿ��������Ӧ��õ�payoff
				double payoff=0;
				for (int j = 0; j < belief2.length; j++) {
					payoff+=(belief2[j]/sum)*B[j][i];
				}
				if(payoff>maxPayoff2) {
					index2=i;
					maxPayoff2=payoff;
				}
			}
			
			//ÿ��agent�����Լ��ĳ�ʼ����
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
			
			//�ж�ÿ��agent��payoff�Ƿ�����
			if(Math.abs(maxPayoff1-lastPayoff1)/maxPayoff1<converge&&Math.abs(maxPayoff2-lastPayoff2)/maxPayoff2<converge) {
				System.out.println("������"+n+"�ε���");
				return new double[] {maxPayoff1,maxPayoff2};
			}
			lastPayoff1=maxPayoff1;
			lastPayoff2=maxPayoff2;
		}
	}
	
	//����agent1��payoff1�Ƚ�FP�����õ�����ʲ�����Ӧ�ĸ���ȷ��ʲ���⣬��ȷ��ʲ�����Ƶ�ʼ�1
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
	
	//���������ʼ�������
	public double[] produceRandomInitialBeliefs(int num) {
		double[] belief=new double[num];
		for (int i = 0; i < belief.length; i++) {
			belief[i]=random.nextDouble()*10000;
		}
		return belief;
	}
	
	//�������������10�����ʼ������¾�ȷ��ʲ������ÿ������ֵ�Ƶ��
	public void calFrequence(double[][] A,double[][] B) {
		double converge=0.0001;  //�ж�fp�㷨���������
		//���о�ȷ��ʲ�����
		getAllExactNE(A, B);
		for (int i = 0; i < 100000; i++) {  //�������������100000�ֳ�ʼ��������fp�����ʲ����
			double[] belief1=produceRandomInitialBeliefs(A[0].length);
			double[] belief2=produceRandomInitialBeliefs(A.length);
			
			//************************************************************
			System.out.println("��"+(i+1)+"��fp:");
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
			
			//���ݳ�ʼ�������fp���һ��������ʲ����
			double[] payoff=findOneNashEquilibria(belief1, belief2, A, B, converge);
			//���ݽ�����ʲ������¶�Ӧ��ȷ��ʲ������ֵ�Ƶ��
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
//		fp.getAllExactNE(A, B);  //�ҵ����о�ȷ��ʲ�����
//		fp.updateFrequence(payoff[0]);  //����fp��������ʲ����������о�ȷ��ʲ�����ж�Ӧ��ʲ�������ֵ�Ƶ��
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
