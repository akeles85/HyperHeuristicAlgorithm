/***********************************************************************
Survivable VT Project with PIC :
Test data creation
This program creates a symmetric VT request file.
Parameters: Regular / Arbitrary
            number of nodes,
            node degree / average node degree

Aysegul Gencata Yayimli, C: September 4, 2008  U: September 5, 2008
***********************************************************************/

#include <iostream>
#include <stdlib.h>
#include <fstream>
#include <math.h>

using namespace std;

struct Request{
   int *VT;
};

int OneConnected (int * S, int N){
   int * g = new int[N * N];
   for (int i = 0; i < N * N; i++)
      g[i] = S[i];

   for (int k = 0; k < N; k++){
      for (int src = 0; src < N; src++)
         if (g[src * N + k] == 1){
            for (int dst = 0; dst < N; dst++){
               if (g[k * N + dst] == 1)
                  g[src * N + dst] = 1;
            }
         }
   }
   for (int i = 0; i < N * N; i++)
      if (g[i] == 0){
         delete g;
         return 0;
      }
   delete g;
   return 1;
}

int TwoConnected (Request & G, int N){
   int *Severed = new int[N * N];
   for (int i = 0; i < N * N; i++)
      Severed[i] = G.VT[i];
   for (int s = 0; s < N; s++)
      for (int d = 0; d < N; d++){
         if (Severed[s * N + d] == 1){
            Severed[s * N + d] = 0;
            if (!OneConnected (Severed, N)){
                delete Severed;
                return 0;
            }
            Severed[s * N + d] = 1;
         }
      }
   delete Severed;
   return 1;
}

int main (int argc, char **argv){
   int Reg, NNodes, NConn, NReq;

/* ---------------------------- Inputs ------------------------------- */
   if (argc < 5){
      clog << "usage: createvt Reg(0/1) Nnodes Nconnections Ntopologies\n";
      exit (1);
   }
   Reg = atoi (argv[1]);
   cout << Reg << " ";
   NNodes = atoi (argv[2]);
   cout << NNodes << " ";
   NConn = atoi (argv[3]); //Number of bi-directional connections
   cout << NConn << " ";
   NReq = atoi (argv[4]);
   cout << NReq << " " << endl;

// Create the requests:
   Request *ReqList;
   if ((ReqList = new Request[NReq]) == NULL)
      clog << "Memory allocation error...";
   Request R;
//clog << "Pit stop 1...";
   int NodeDegree = 2 * NConn / NNodes;
   if (Reg && (2 * float(NConn) / float (NNodes) - NodeDegree > 0)){
      cout << "We cannot create such regular topology.\n";
      delete ReqList;
      exit (1);
   }
   if (NConn > (NNodes * (NNodes - 1) / 2)) {
      cout << "Too many connections.\n";
      delete ReqList;
      exit (1);
   }
   bool done = false;
   int VTcounter = 0;
   int Samecount = 0;
   R.VT = new int[NNodes * NNodes];
   do{
      int Tx[NNodes];
      int conncnt, s, d, i;
      for (i = 0; i < NNodes * NNodes; i++)
         R.VT[i] = 0;
      for (i = 0; i < NNodes; i++)
         Tx[i] = 0;
   // create connections:
      conncnt = 0;
      int x;
   	  int noAvailableNode;

      if (NConn == (NNodes * (NNodes - 1) / 2)) { //Complete topology
         for (i = 0; i < NNodes * NNodes; i++)
            R.VT[i] = 1;
         done = true;
         cout << "This is a complete topology, only 1 VT is created.\n";
      }
      else{
    	 noAvailableNode = 0;
         while ((conncnt < NConn) && (noAvailableNode < 100)){
            x = int(float(rand()) / float(RAND_MAX) * NNodes);
            if (x == NNodes) x--;
            s = x;
            x = int(float(rand()) / float(RAND_MAX) * NNodes);
            if (x == NNodes) x--;
            d = x;
            if ((R.VT[s * NNodes + d] == 0) && (s != d)){ //new connection
               if ((Reg && Tx[s] < NodeDegree && Tx[d] < NodeDegree)
                  || (!Reg)) {
                  R.VT[s * NNodes + d] = 1;
                  R.VT[d * NNodes + s] = 1;
                  conncnt++;
                  Tx[s]++;
                  Tx[d]++;
//                  cout << "s= " << s << " d= " << d << endl;
               }
               else{
            	   noAvailableNode ++;
//            	   cout << "s= " << s << " d= " << d << " Tx[" << s << "] = " << Tx[s] << " Tx[" << d << "] = " << Tx[d] << endl;
               }
            }
//            else cout << "s= " << s << " d= " << d << "no connection created" << endl;
         }
      }
      if (noAvailableNode == 100){
//    	cout << "Wrong VT" << endl;
      }
      else{
		  bool StoreThisVT;
		  // Is the VT 2-connected?
		  if (TwoConnected (R, NNodes)){
			 // Compare the new VT to the ones in the list
			 StoreThisVT = true;
			 for (int k = 0; (k < VTcounter && StoreThisVT); k++){
				int m = 0;
				while ((ReqList[k].VT[m] == R.VT[m]) && (m < NNodes * NNodes))
				   m++;
				if (m == NNodes * NNodes) // The VTs are the same
				   StoreThisVT = false;
			 }
			 // Add the new VT to the list
			 if (StoreThisVT){
				ReqList[VTcounter].VT = new int[NNodes * NNodes];
				for (int k = 0; k < NNodes * NNodes; k++)
				   ReqList[VTcounter].VT[k] = R.VT[k];
				VTcounter++;
			 }
			 else
				Samecount++; // If reached 10 then end program
		  }
		  // This else section is added only for program check pupose
		  // Can be eliminated after I trust the program works fine!
		  else{
	  /*       clog << "This topology is rejected, because it is not reliable.\n";
			 for (int src = 0; src < NNodes; src++){
				for (int dst = 0; dst < NNodes; dst++)
				   clog << R.VT[src * NNodes + dst] << " ";
				clog << endl;
			 }
		*/ }
		  cout << VTcounter << " topologies created " << endl;
		  if (Samecount >= 10){
			 done = true;
			 cout << "10 same topology " << endl;
		  }
		  if (VTcounter == NReq) done = true;
      }
   }while (!done);
   delete R.VT;
clog << "Everything to the file...\n";
// Prepare the output file:
   ofstream outfile;
//   outfile.open (argv[4]);
/*   if (Reg) outfile << "Reg ";
   else outfile << "Ran ";
   outfile << NReq << " " << NNodes << " " << NConn << endl;
*/// Output the connections to file
   char outfilename[20];
   for (int i = 0; i < VTcounter; i++){
	  sprintf(outfilename, "nsfnet_vt_%d.txt",i);
	  outfile.open(outfilename);
      outfile << argv[2] << endl << argv[3] << endl << endl;
      int s, d;
      for (s = 0; s < NNodes; s++){
         for (d = s; d < NNodes; d++){
        	 if (ReqList[i].VT[s * NNodes + d] == 1)
        		 outfile << s << " " << d << endl;
         }
//        	 outfile << ReqList[i].VT[s * NNodes + d] << " ";
//         outfile << ReqList[i].VT[s * NNodes + d] << endl;
      }
      outfile.close ();
   }
// Delete the ReqList
   for (int i = 0; i < VTcounter; i++)
      delete ReqList[i].VT;
   return 0;
}
