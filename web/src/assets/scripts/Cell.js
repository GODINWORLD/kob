export class Cell{
    constructor(r, c){
        this.r = r;
        this.c = c;
        this.x = c + 0.5; //x和y是相对于canvas坐标系的坐标，和数组坐标相反
        this.y = r + 0.5;
    }
}