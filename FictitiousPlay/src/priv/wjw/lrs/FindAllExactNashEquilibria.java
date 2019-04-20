package priv.wjw.lrs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lse.math.games.Rational;
import lse.math.games.lrs.LrsAlgorithm;
import lse.math.games.matrix.BimatrixSolver;
import lse.math.games.matrix.Equilibria;
import lse.math.games.matrix.Equilibrium;

public class FindAllExactNashEquilibria {

	private Rational[][] a;
	private Rational[][] b;
	public List<Solution> solutions = new ArrayList<>();  //以Solution的形式保存所有的纳什均衡解

	//求出所有准确的纳什均衡解并以Solution的形式存到集合solutions中，初始化每个解出现的频率为0，返回payoff1s(每个解中agent1的payoff组成的数组)
	public double[] getAllEquilibria(double[][] A,double[][] B) {
		Equilibria equilibria=findAllEquilibria(A, B);
		Iterator<Equilibrium> iterator = equilibria.iterator();
		while(iterator.hasNext()) {
			Equilibrium equilibrium = iterator.next();
			double payoff1=equilibrium.payoff1.doubleValue();
			double payoff2=equilibrium.payoff2.doubleValue();
			double[] probVec1=transformRationalToDouble(equilibrium.probVec1);
			double[] probVec2=transformRationalToDouble(equilibrium.probVec2);
			solutions.add(new Solution(probVec1,probVec2,payoff1,payoff2));
		}
		
		double[] payoff1s=new double[solutions.size()];
		for (int i = 0; i < payoff1s.length; i++) {
			payoff1s[i]=solutions.get(i).getPayoff1();
		}
		return payoff1s;
	}

	public Equilibria findAllEquilibria(double[][] A, double[][] B) {
		transformInput(A, B);
		LrsAlgorithm lrs = new LrsAlgorithm();
		BimatrixSolver bs = new BimatrixSolver();
		Equilibria equilibria = bs.findAllEq(lrs, a, b);
		return equilibria;
	}

	// 将double[][]形式的输入矩阵转换为Rational[][]
	public void transformInput(double[][] A, double[][] B) {
		int row = A.length;
		int col = A[0].length;
		a = new Rational[row][col];
		b = new Rational[row][col];
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				a[i][j] = Rational.valueOf(A[i][j]);
				b[i][j] = Rational.valueOf(B[i][j]);
			}
		}
	}

	// 将Rational[]形式的数组转换为double[]
	public double[] transformRationalToDouble(Rational[] arr) {
		double[] result = new double[arr.length];
		for (int i = 0; i < arr.length; i++) {
			result[i] = arr[i].doubleValue();
		}
		return result;
	}
	
	public static void print(double[] arr) {
		for (int i = 0; i < arr.length; i++) {
			System.out.println(arr[i]);
		}
	}
	
	public static void main(String[] args) {
		double[][] A = new double[10][10];
		double[][] B = new double[10][10];
		String str1 = "0.0,0.0 0.0,0.0 0.0,0.0 0.0,0.0 0.0,0.0 0.0,0.0 5017.703281395293,17659.871792260485 0.0,0.0 0.0,0.0 1847.4563310228073,7338.905879691402";
		String str2 = "0.0,0.0 0.0,0.0 0.0,0.0 0.0,0.0 0.0,0.0 0.0,0.0 0.0,0.0 4475.714980535099,13459.037986158688 0.0,0.0 0.0,0.0";
		String str3 = "1701.7,2412.9 0.0,0.0 0.0,0.0 1300.0,1680.0 1350.0,2070.0 0.0,0.0 0.0,0.0 0.0,0.0 0.0,0.0 0.0,0.0";
		String str4 = "0.0,0.0 0.0,0.0 0.0,0.0 1925.0,1575.0 0.0,0.0 0.0,0.0 0.0,0.0 0.0,0.0 1785.0,4165.0 0.0,0.0";
		String str5 = "6438.9591,7910.9982 0.0,0.0 0.0,0.0 0.0,0.0 0.0,0.0 2160.0,2860.0 2520.0,2860.0 0.0,0.0 0.0,0.0 2295.0,4655.0";
		String str6 = "0.0,0.0 2695.0,765.0 0.0,0.0 0.0,0.0 0.0,0.0 0.0,0.0 0.0,0.0 0.0,0.0 0.0,0.0 0.0,0.0";
		String str7 = "0.0,0.0 6373.07513828134,14246.226607037876 0.0,0.0 0.0,0.0 0.0,0.0 0.0,0.0 0.0,0.0 0.0,0.0 0.0,0.0 5352.810169846107,11396.148210990143";
		String str8 = "0.0,0.0 0.0,0.0 0.0,0.0 2550.0,2310.0 0.0,0.0 0.0,0.0 0.0,0.0 7788.99544275,12381.97015575 4132.65,6965.85 4425.0,3895.0";
		String str9 = "0.0,0.0 0.0,0.0 0.0,0.0 0.0,0.0 0.0,0.0 0.0,0.0 4730.85,7242.95 0.0,0.0 0.0,0.0 0.0,0.0";
		String str10 = "0.0,0.0 3541.85,4469.85 0.0,0.0 0.0,0.0 3610.0,2790.0 0.0,0.0 0.0,0.0 0.0,0.0 0.0,0.0 0.0,0.0";
		String strs[] = { str1, str2, str3, str4, str5, str6, str7, str8, str9, str10 };
		for (int i = 0; i < strs.length; i++) {
			String[] num = strs[i].split(" ");
			for (int j = 0; j < num.length; j++) {
				double d1 = Double.valueOf(num[j].split(",")[0]);
				double d2 = Double.valueOf(num[j].split(",")[1]);
				A[i][j] = d1;
				B[i][j] = d2;
			}
		}
		
		FindAllExactNashEquilibria fene=new FindAllExactNashEquilibria();
		double[] payoff1s=fene.getAllEquilibria(A, B);
		print(payoff1s);
	}
}
