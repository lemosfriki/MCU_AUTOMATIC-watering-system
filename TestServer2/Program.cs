using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.IO;
using System.IO.Ports;
using System.Net;
using System.Net.Sockets;
using System.Threading;
using System.Timers;

namespace TestServer2
{
    class ProgramS
    {
        public static string host;
        public static int port = 10000;
        public static int iparr = 0; 
        

        static void Main(string[] args)
        {
            StreamWriter swr = new StreamWriter(new FileStream("logger1.txt", FileMode.Append));
            string comport;
            Console.Write("Input Server Address : ");
            host = Console.ReadLine();
            Console.Write("COM Number : ");
            comport = Console.ReadLine();

            //직렬 포트 개방
            SerialPort sp = new SerialPort();
            sp = new SerialPort();
            sp.PortName = "COM" + comport;
            sp.BaudRate = 9600;

            IPHostEntry ih = Dns.GetHostEntry(host);
            //ih.AddressList[iparr] 자동 조정
            while(true)
            {
                if (host == ih.AddressList[iparr].ToString())
                    break;
                iparr++;
            }
            TcpListener tl = new TcpListener(ih.AddressList[iparr], port);
            tl.Start();
            

            //연결 대기
            Console.WriteLine("Waiting.... Server IP : " + ih.AddressList[iparr]);
            swr.WriteLine("Waiting.... Server IP : " + ih.AddressList[iparr]);
            while (true)
            {
                TcpClient tc = tl.AcceptTcpClient();
                IPEndPoint ip_point = (IPEndPoint)tc.Client.RemoteEndPoint;
                string ip = ip_point.Address.ToString();
                Console.WriteLine("Connected! Client : {0}\t\t{1}\n", ip, DateTime.Now.ToString("F"));
                swr.WriteLine("Connected! Client : {0}\t\t{1}\n", ip, DateTime.Now.ToString("F"));
                Client c = new Client(tc, comport, swr, sp);
                Thread th = new Thread(c.run);
                th.Start();
            }
        }
    }
    class Client
    {
        TcpClient tc;
        String comport;
        StreamWriter swr;
        SerialPort sp;
        

        public Client(TcpClient c, String comport, StreamWriter swr, SerialPort sp)
        {
            tc = c;
            this.comport = comport;
            this.swr = swr;
            this.sp = sp;
        }
        
        
        public void run()
        {
            
            //실시간 센서 값 처리를 위한 timer
            System.Timers.Timer timer = new System.Timers.Timer();
            timer.Interval = 1000; //해당 시간마다 writeSensorData 실행(단위 : 밀리세컨드)
            timer.Elapsed += new ElapsedEventHandler(writeSensorData);
            timer.Start();      

            StreamWriter sw = new StreamWriter(tc.GetStream());
            StreamReader sr = new StreamReader(tc.GetStream());
            Watering wt = new Watering();
            string str;
            IPEndPoint ip_point = (IPEndPoint)tc.Client.RemoteEndPoint;
            while (true)
            {
                str = "";
                try
                {
                    //Console.WriteLine("Trying to read message...");
                    while (true)
                    {
                        char rd = (char)sr.Read();
                        if (rd == '*')
                            break;
                        str += rd;
                    }
                    Console.WriteLine("Message From Client {0} : {1}", ip_point.Address, str);
                    swr.WriteLine("Message From Client {0} : {1}", ip_point.Address, str);

                    if (str.Contains("refresh")) //새로고침 명령
                    {
                        if(sp.IsOpen)
                            sp.Write("refresh*");
                        Thread.Sleep(100);
                        str = sp.ReadLine();
                    }

                    else if (str.Contains("water")) //수동급수 명령
                    {
                        if(sp.IsOpen)
                            sp.Write("water " + wt.GetWaterTime(str).ToString() + "*");
                        str = "Run Pump For " + wt.GetWaterTime(str) + " Seconds";
                    }

                    else if (str.Contains("Humid")) //토양수분 설정 명령
                    {
                        if(sp.IsOpen)
                            sp.Write("Humid " + wt.GetWaterTime(str).ToString() + "*");
                        str = "Set Watering Humid to " + wt.GetHumid(str) + ".";
                    }
                    else if (str.Contains("HTTP")) //웹 접속시도 응답
                    {
                        str = "403 FORBIDDEN";
                        
                    }

                    //클라이언트에 전송
                    sw.WriteLine(str); 
                    sw.Flush();
                    //콘솔과 파일에 출력
                    Console.WriteLine("Sent Message : {0}\n", str);
                    swr.WriteLine("Sent Message : {0}\n", str);
                    swr.Flush();
                    
                }
                catch
                {
                    sr.Close();
                    sw.Close();
                    tc.Close();
                }
                
            }
            //실시간 센서 데이터 수집
            void writeSensorData(object sender, ElapsedEventArgs e)
            {
                String Sensor = "refresh";
                if(!sp.IsOpen)sp.Open();
                sp.Write(Sensor + "*");
                Thread.Sleep(100);
                Sensor = sp.ReadLine();
                Console.WriteLine("Sensor Data : {0}", Sensor);
                Console.WriteLine("{0}", DateTime.Now.ToString("F"));
                swr.Write("Sensor Data : {0}", Sensor);
                swr.WriteLine("{0}", DateTime.Now.ToString("F"));
                swr.Flush();
            }
            
        }

        
    }

    class Watering
    {
        private int WaterTime;
        private int Humid;
        public int GetWaterTime(string str)
        {
            WaterTime = 10;
            if (str.Contains(" "))
            {
                int spaceindex = str.IndexOf(" ");
                WaterTime = int.Parse(str.Substring(spaceindex + 1));
            }
            return WaterTime;
        }
        public int GetHumid(string str)
        {
            Humid = 0;
            if(str.Contains(" "))
            {
                int spaceindex = str.IndexOf(" ");
                Humid = int.Parse(str.Substring(spaceindex + 1));
            }
            return Humid;
        }
    }
}
