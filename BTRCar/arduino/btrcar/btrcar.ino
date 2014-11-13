char data[30];
int pos = 0;

void setup() {
  // initialize serial communication at 9600 bits per second:
  Serial.begin(9600);
  
  //Setup Channel A
  pinMode(12, OUTPUT); //Initiates Motor Channel A pin
  pinMode(9, OUTPUT); //Initiates Brake Channel A pin
  
  //Setup Channel B
  pinMode(13, OUTPUT); //Initiates Motor Channel A pin
  pinMode(8, OUTPUT); //Initiates Brake Channel A pin
}

void loop() {
  uint8_t chr;

  while(Serial.available()) {
    chr = (uint8_t)Serial.read();
    if (chr == '$') {
      memset(data, 0x00, sizeof(data));
      pos = 0;
    }
    else if (chr == ';'){
      int direction = 0;
      int speed = 0;
      char strSpeed[10];
      
      if (pos != 9) {
        memset(data, 0x00, sizeof(data));
        pos = 0;
        continue;
      }
      
      if (data[0] == 'L') {
        direction = LOW;
        Serial.print("LEFT : ");
      }
      else if (data[0] == 'R') {
        direction = HIGH;
        Serial.print("RIGHT : ");
      }
      
      strSpeed[0] = data[1];
      strSpeed[1] = data[2];
      strSpeed[2] = data[3];
      strSpeed[3] = 0;
      
      speed = atoi(strSpeed);
      Serial.print(speed);
      Serial.print(", ");
      
      //Motor B backward @ half speed
      digitalWrite(13, direction); //Establishes backward direction of Channel B
      digitalWrite(8, LOW); //Disengage the Brake for Channel B
      analogWrite(11, speed); //Spins the motor on Channel B at half speed
      
      if (data[5] == 'B') {
        direction = LOW;
        Serial.print("BACKWARD :");
      }
      else if (data[5] == 'F') {
        direction = HIGH;
        Serial.print("FORWARD :");
      }
      
      strSpeed[0] = data[6];
      strSpeed[1] = data[7];
      strSpeed[2] = data[8];
      strSpeed[3] = 0;
      
      speed = atoi(strSpeed);
      Serial.println(speed);
      
      //Motor A forward @ full speed
      digitalWrite(12, direction); //Establishes forward direction of Channel A
      digitalWrite(9, LOW); //Disengage the Brake for Channel A
      analogWrite(3, speed); //Spins the motor on Channel A at full speed
      
      memset(data, 0x00, sizeof(data));
      pos = 0;
    }
    else if (pos < 29) {
      data[pos++] = chr;
    }
    else {
      memset(data, 0x00, sizeof(data));
      pos = 0;
    }
  }
  
  delay(1);
}
