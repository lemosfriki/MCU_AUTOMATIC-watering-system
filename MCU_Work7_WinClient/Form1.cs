using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using System.IO;
using System.Net;
using System.Net.Sockets;
using System.Threading;

namespace work7
{
    public partial class Form1 : Form
    {
        public static int port = 10000;
        private TcpClient tc;
        private StreamReader sr;
        private StreamWriter sw;

        public Form1()
        {
            InitializeComponent();
            pictureBox1.SizeMode = PictureBoxSizeMode.StretchImage;
            pictureBox1.Load("edit_pic_back_1.jpg");
            

        }

        private void button1_Click(object sender, EventArgs e)
        {
            sw.Write("Humid " + numericUpDown2.Value + "*");
            sw.Flush();
            sr.ReadLine();
        }

        private void button2_Click(object sender, EventArgs e)
        {
            sw.Write("water " + numericUpDown1.Value + "*");
            sw.Flush();
            sr.ReadLine();
        }

        private void button3_Click(object sender, EventArgs e)
        {
            numericUpDown1.Value = 0;
            label6.Text = "0";
            label7.Text = "0";
            label8.Text = "0";
            chart1.Series[0].Points.Clear();
            chart1.Series[1].Points.Clear();
        }

        private void button4_Click(object sender, EventArgs e)
        {
            string[] a;
            sw.Write("refresh*");
            sw.Flush();
            a = sr.ReadLine().Split(' ');
            label8.Text = a[0];
            label7.Text = a[1];
            label6.Text = a[2];
            sr.ReadLine();
            chart1.Series[0].Points.Clear();
            chart1.Series[1].Points.Clear();
            chart1.Series[0].Points.Add(float.Parse(a[1]));
            chart1.Series[1].Points.Add(float.Parse(a[2]));
        }

        private void button5_Click(object sender, EventArgs e)
        {
            sw.Write("stop*");
            sw.Flush();
            sw.Close();
            sr.Close();
            tc.Close();
            Close();
        }
        public void run()
        {
            tc = new TcpClient(targetip.Text, port);
            sr = new StreamReader(tc.GetStream());
            sw = new StreamWriter(tc.GetStream());
            if (tc.Connected)
                MessageBox.Show("Connected");
        }

        private void Form1_Shown(object sender, EventArgs e)
        {
            
        }

        private void btn_connect_Click(object sender, EventArgs e)
        {
            Thread th = new Thread(this.run);
            th.Start();
            Thread.Sleep(500);
            
        }
    }
}