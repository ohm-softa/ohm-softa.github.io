using System;

namespace MixinCaveats {
    class Message {
        public string Text { get; set; }
    }
    interface IEscalatable {
        string Text { get; }
    }

    class UnicodeMessage : Message {
        public string UnicodeText { get; set; }
    }

    class EscalatableUnicodeMessage : UnicodeMessage, IEscalatable {
        
    }
    class DynamicMessage : Message, IEscalatable {
        public string Escalated1() => Text.ToLower();
    
        public void Sophisticated1(object o) {
            Console.WriteLine("Message: {0}", o.GetType());
        }
        public void Sophisticated2(int i) {
            Console.WriteLine("Message: {0}", i.GetType());
        }
    }
    
    static class MessageMixins {
        public static string Escalated1(this IEscalatable self) 
            => self.Text.ToUpper();
        
        public static void Sophisticated1(this Message self, int i)
            => Console.WriteLine("Mixin: {0}", i);

        public static void Sophisticated2(this Message self, object o) 
            => Console.WriteLine("Mixin: {0}", o);
    }

    class Program {
        // static void Main(string[] args) {
        //     DynamicMessage m = new DynamicMessage();
        //     m.Text = "Hello, world";

        //     Console.WriteLine(m.Escalated1());  // oops! Message.Escalated1
        //     Console.WriteLine((m as IEscalatable).Escalated1());  // ah!

        //     // watch out with automatic type conversion
        //     m.Sophisticated1(1);
        //     m.Sophisticated1("Hans");

        //     m.Sophisticated2(1);
        //     m.Sophisticated2("Hans");
        // }
    }
}